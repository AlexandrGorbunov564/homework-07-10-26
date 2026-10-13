const APP_PATH = window.APP_CONTEXT_PATH || "";
let latestPaymentRequestInProgress = false;

async function updateTaxRate() {
    try {
        const response = await fetch(APP_PATH + "/api/tax/rate", {
            cache: "no-store", credentials: "same-origin"
        });
        if (!response.ok) throw new Error("HTTP " + response.status);
        const data = await response.json();
        if (data.rate !== undefined) {
            const percent = document.getElementById("percent");
            if (percent) percent.textContent = data.rate + "%";
        }
    } catch (error) {
        console.error("Не удалось обновить ставку:", error);
    }
}

function displayLatestPayment(payment) {
    const tbody = document.getElementById("payments-history-body");
    if (!tbody || !payment) return;
    let row = Array.from(tbody.querySelectorAll("tr")).find(function (tr) {
        return tr.dataset.paymentId === String(payment.id)
            || (tr.cells.length > 0 && tr.cells[0].textContent.trim() === String(payment.id));
    });

    if (!row) {
        row = document.createElement("tr");
        ["id", "income", "percent", "tax", "createdAt"].forEach(function (field) {
            const cell = document.createElement("td");
            cell.dataset.field = field;
            row.appendChild(cell);
        });
        tbody.prepend(row);
    }
    row.dataset.paymentId = String(payment.id);

    ["id", "income", "percent", "tax", "createdAt"].forEach(function (field, index) {
        const cell = row.querySelector('[data-field="' + field + '"]') || row.cells[index];
        if (cell) {
            cell.dataset.field = field;
            cell.textContent = payment[field] == null ? "" : String(payment[field]);
        }
    });
}

async function refreshHistoryFromPage() {
    const response = await fetch(APP_PATH + "/tax", {
        cache: "no-store", credentials: "same-origin",
        headers: {"Accept": "text/html"}
    });
    if (!response.ok || response.redirected) {
        throw new Error("Не удалось получить историю, HTTP " + response.status);
    }
    const html = await response.text();
    const page = new DOMParser().parseFromString(html, "text/html");
    const freshBody = page.getElementById("payments-history-body");
    const currentBody = document.getElementById("payments-history-body");
    if (!freshBody || !currentBody) {
        throw new Error("Таблица истории не найдена в ответе сервера");
    }
    currentBody.replaceChildren(...Array.from(freshBody.children).map(function (row) {
        return document.importNode(row, true);
    }));
}

async function updateLatestPayment() {
    if (latestPaymentRequestInProgress) return;
    latestPaymentRequestInProgress = true;
    try {
        const response = await fetch(APP_PATH + "/api/payments/latest", {
            cache: "no-store", credentials: "same-origin"
        });
        if (response.status === 204) {
            const tbody = document.getElementById("payments-history-body");
            if (tbody) tbody.replaceChildren();
            return;
        }
        if (!response.ok) throw new Error("HTTP " + response.status);
        displayLatestPayment(await response.json());
    } catch (error) {
        console.warn("API последнего платежа недоступен, обновляем историю через /tax:", error);
        try {
            await refreshHistoryFromPage();
        } catch (fallbackError) {
            console.error("Не удалось обновить историю платежей:", fallbackError);
        }
    } finally {
        latestPaymentRequestInProgress = false;
    }
}

function refreshTaxPage() {
    updateTaxRate();
    updateLatestPayment();
}

refreshTaxPage();
setInterval(refreshTaxPage, 3000);