(() => {
  const status = document.querySelector("#connection-status");
  const statusText = status?.querySelector("span:last-child");
  let reconnectTimer;
  let pendingRefresh = false;
  const onDialogClosed = () => {
    if (pendingRefresh) location.reload();
  };

  const setConnection = (state, text) => {
    if (!status) return;
    status.dataset.state = state;
    statusText.textContent = text;
  };

  const refreshResults = async () => {
    try {
      const response = await fetch(location.href, {
        headers: { "X-Requested-With": "XMLHttpRequest" },
      });
      if (!response.ok || response.redirected) return;
      const updated = new DOMParser().parseFromString(
        await response.text(),
        "text/html",
      );
      [".operation-results", ".operation-result"].forEach((selector) => {
        const current = document.querySelector(selector),
          next = updated.querySelector(selector);
        if (current && next) current.replaceWith(next);
      });
      const current = document.querySelector("#coordinates-id");
      const next = updated.querySelector("#coordinates-id");
      if (current && next) {
        const value = current.value;
        current.replaceChildren(...next.children);
        current.value = value;
        if (value && !current.value)
          showLiveNotice("Выбранные координаты удалены. Выберите другие.");
        syncCoordinates();
      }
    } catch {
      showLiveNotice("Не удалось обновить данные. Обновите страницу.");
    }
  };

  const showLiveNotice = (message) => {
    let notice = document.querySelector(".live-notice");
    if (!notice) {
      notice = document.createElement("div");
      notice.className = "live-notice";
      notice.setAttribute("role", "status");
      document.body.appendChild(notice);
    }
    notice.textContent = message;
    notice.classList.add("visible");
    window.setTimeout(() => notice.classList.remove("visible"), 4200);
  };

  const connect = () => {
    const protocol = location.protocol === "https:" ? "wss:" : "ws:";
    const socket = new WebSocket(`${protocol}//${location.host}/ws/vehicles`);

    socket.addEventListener("open", () => setConnection("online", "Онлайн"));
    socket.addEventListener("message", (event) => {
      const change = JSON.parse(event.data);
      if (
        change.operation === "DELETED" &&
        String(change.vehicleId) === document.body.dataset.vehicleId
      ) {
        location.assign("/vehicles");
        return;
      }
      const mode = document.body.dataset.liveUpdates;
      if (mode === "reload" && !document.querySelector("dialog[open]")) {
        showLiveNotice("Данные изменились в другом окне. Обновляем…");
        window.setTimeout(() => location.reload(), 650);
      } else {
        if (mode === "reload") pendingRefresh = true;
        showLiveNotice("Реестр обновлён");
        refreshResults();
      }
    });
    socket.addEventListener("close", () => {
      setConnection("offline", "Нет связи");
      window.clearTimeout(reconnectTimer);
      reconnectTimer = window.setTimeout(connect, 2500);
    });
    socket.addEventListener("error", () => socket.close());
  };

  document.querySelectorAll("form[data-confirm]").forEach((form) => {
    form.addEventListener("submit", (event) => {
      if (!window.confirm(form.dataset.confirm)) event.preventDefault();
    });
  });

  document.querySelectorAll("input[data-positive]").forEach((input) => {
    const validate = () =>
      input.setCustomValidity(
        input.value && Number(input.value) <= 0
          ? "Значение должно быть больше 0"
          : "",
      );
    input.addEventListener("input", validate);
    validate();
  });

  const coordinateChoice = document.querySelector("#coordinates-id");
  const syncCoordinates = () => {
    const selected = coordinateChoice?.selectedOptions[0];
    const existing = Boolean(coordinateChoice?.value);
    ["x", "y"].forEach((axis) => {
      const input = document.querySelector("#coordinate-" + axis);
      if (!input) return;
      input.readOnly = existing;
      input.required = !existing;
      if (existing) input.value = selected.dataset[axis];
    });
  };
  coordinateChoice?.addEventListener("change", syncCoordinates);
  syncCoordinates();

  document.querySelectorAll("[data-open-dialog]").forEach((button) =>
    button.addEventListener("click", () => {
      document.getElementById(button.dataset.openDialog).showModal();
    }),
  );
  document
    .querySelectorAll("dialog")
    .forEach((dialog) => dialog.addEventListener("close", onDialogClosed));
  document
    .querySelectorAll("[data-close-dialog]")
    .forEach((button) =>
      button.addEventListener("click", () => button.closest("dialog").close()),
    );

  document.querySelectorAll("a[data-edit]").forEach((link) =>
    link.addEventListener("click", (event) => {
      event.preventDefault();
      const modal = document.createElement("dialog");
      modal.className = "edit-dialog";
      modal.setAttribute("aria-label", "Изменение транспортного средства");
      const bar = document.createElement("div");
      bar.className = "dialog-bar";
      bar.textContent = "Изменение транспортного средства";
      const close = document.createElement("button");
      close.type = "button";
      close.className = "text-button";
      close.textContent = "Закрыть";
      close.addEventListener("click", () => modal.close());
      bar.appendChild(close);
      const frame = document.createElement("iframe");
      frame.title = "Форма изменения транспортного средства";
      frame.src = link.href + "?dialog=true";
      frame.addEventListener("load", () => {
        if (frame.contentDocument?.body.dataset.vehicleId) {
          modal.close();
          location.reload();
        }
      });
      modal.append(bar, frame);
      document.body.appendChild(modal);
      modal.addEventListener("close", () => {
        modal.remove();
        onDialogClosed();
      });
      modal.showModal();
    }),
  );

  if (status) connect();
})();
