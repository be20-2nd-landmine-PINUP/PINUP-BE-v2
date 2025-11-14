// 사이드바 누른 곳 활성화
window.addEventListener("load", () => {
    const current = window.location.pathname;

    document.querySelectorAll(".nav-item").forEach(item => {
        const href = item.getAttribute("href");
        if (!href) return;

        if (href && current.startsWith(href)) {
            item.classList.add("active");
        }
    });
});

// SSE 연결 (로그인된 사용자 ID로 변경)
const eventSource = new EventSource(`/sse/connect/1`);

eventSource.addEventListener("connect", (event) => {
    console.log("✅ SSE 연결 성공:", event.data);
});

eventSource.addEventListener("new notification", (event) => {
    const data = JSON.parse(event.data);
    addNotification(data.notificationMessage);
});

eventSource.onerror = (err) => {
    console.error("🚨 SSE 연결 오류:", err);
};

// 알림 추가 함수
function addNotification(message) {
    const list = document.getElementById("notif-list");
    const item = document.createElement("li");
    item.textContent = "📩 " + message;
    list.prepend(item);

    // 숫자 뱃지 증가
    const badge = document.getElementById("notif-count");
    let count = parseInt(badge.textContent) || 0;
    badge.textContent = count + 1;
    badge.style.display = "inline-block";
}

// 알림창 토글
const notifBtn = document.getElementById("notif-btn");
const notifBox = document.getElementById("notif-box");
const notifBadge = document.getElementById("notif-count");

if (notifBtn && notifBox && notifBadge) {
    notifBtn.addEventListener("click", function (e) {
        e.preventDefault();
        notifBox.classList.toggle("show");

        if (notifBox.classList.contains("show")) {
            notifBadge.textContent = "0";
            notifBadge.style.display = "none";
        }
    });

    // 바깥 클릭 시 닫기
    window.addEventListener("click", function (event) {
        if (!notifBtn.contains(event.target)) {
            notifBox.classList.remove("show");
        }
    });
}