// ================================
// INITIAL LOAD
// ================================
document.addEventListener("DOMContentLoaded", () => {
  showPublicNav();
  loadPage("about.html");
});

// ================================
// PAGE LOADER (SPA CORE)
// ================================
function loadPage(page) {
  fetch("partials/" + page, { cache: "no-store" })
    .then(r => {
      if (!r.ok) throw new Error("Page load failed");
      return r.text();
    })
    .then(html => {
      document.getElementById("app-content").innerHTML = html;

      if (page === "faculty-login.html") initFacultyLogin();
      if (page === "student-login.html") initStudentLogin();
      if (page === "create-event.html") initCreateEvent();
      if (page === "student-dashboard.html") loadStudentDashboard();
      if (page === "upcoming-events.html") loadUpcomingEvents();
    })
    .catch(err => console.error(err));
}

// ================================
// NAVBARS
// ================================
function showPublicNav() {
  document.getElementById("navTitle").innerText = "Event Scheduler";
  document.getElementById("navLinks").innerHTML = `
    <a href="#" onclick="loadPage('about.html')">Home</a>
    <a href="#" onclick="loadPage('faculty-login.html')">Faculty</a>
    <a href="#" onclick="loadPage('student-login.html')">Student</a>
    <a href="#" onclick="logout()">Logout</a>
  `;
}

function showFacultyNav() {
  document.getElementById("navTitle").innerText = "Faculty Dashboard";
  document.getElementById("navLinks").innerHTML = `
    <a href="#" onclick="loadPage('faculty-dashboard.html')">Dashboard</a>
    <a href="#" onclick="loadPage('create-event.html')">Create Event</a>
    <a href="#" onclick="loadPage('upcoming-events.html')">Upcoming</a>
    <a href="#" onclick="logout()">Logout</a>
  `;
}

function showStudentNav() {
  document.getElementById("navTitle").innerText = "Student Dashboard";
  document.getElementById("navLinks").innerHTML = `
    <a href="#" onclick="loadPage('student-dashboard.html')">Dashboard</a>
    <a href="#" onclick="loadPage('upcoming-events.html')">Upcoming</a>
    <a href="#" onclick="logout()">Logout</a>
  `;
}

// ================================
// FACULTY LOGIN
// ================================
function initFacultyLogin() {
  const form = document.getElementById("facultyLoginForm");
  if (!form) return;

  form.addEventListener("submit", e => {
    e.preventDefault();

    fetch("/Event_Management_System/FacultyLoginServlet", {
      method: "POST",
      body: new FormData(form)
    })
    .then(r => r.text())
    .then(res => {
      if (res.trim() === "success") {
        showFacultyNav();
        loadPage("faculty-dashboard.html");
      } else {
        document.getElementById("loginError").innerText =
          "Invalid faculty credentials";
      }
    });
  });
}

// ================================
// STUDENT LOGIN
// ================================
function initStudentLogin() {
  const form = document.getElementById("studentLoginForm");
  if (!form) return;

  form.addEventListener("submit", e => {
    e.preventDefault();

    fetch("/Event_Management_System/StudentLoginServlet", {
      method: "POST",
      body: new FormData(form)
    })
    .then(res => res.text())
    .then(text => {
      if (text.trim() === "success") {

        sessionStorage.setItem("studentName", "ANANTHI G");
        sessionStorage.setItem("studentEmail", "ananthi.2301013@srec.ac.in");

        showStudentNav();
        loadPage("student-dashboard.html");

      } else {
        document.getElementById("loginError").innerText =
          "Invalid student credentials";
      }
    })
    .catch(() => alert("Server error"));
  });
}

// ================================
// CREATE EVENT
// ================================
function initCreateEvent() {
  const form = document.getElementById("eventForm");
  if (!form) return;

  form.addEventListener("submit", e => {
    e.preventDefault();

    fetch("/Event_Management_System/EventServlet", {
      method: "POST",
      body: new FormData(form)
    })
    .then(r => r.text())
    .then(res => {
      if (res.trim() === "success") {
        alert("Event created successfully");
        loadPage("faculty-dashboard.html");
      } else {
        alert("Failed to create event");
      }
    });
  });
}

// ================================
// STUDENT DASHBOARD
// ================================
function loadStudentDashboard() {
  document.getElementById("studentName").innerText =
    sessionStorage.getItem("studentName");

  document.getElementById("studentEmail").innerText =
    sessionStorage.getItem("studentEmail");
}

// ================================
// UPCOMING EVENTS
// ================================
function loadUpcomingEvents() {
  fetch("/Event_Management_System/GetUpcomingEventsServlet")
    .then(r => r.json())
    .then(events => {
      const box = document.getElementById("eventsContainer");
      box.innerHTML = "";

      if (events.length === 0) {
        box.innerHTML = "<p>No upcoming events</p>";
        return;
      }

      events.forEach(e => {
        box.innerHTML += `
          <div class="event-card">
            <h3>${e.name}</h3>
            <p><b>Date:</b> ${e.start} → ${e.end}</p>
            <p><b>Venue:</b> ${e.venue}</p>
            <p><b>Seats Left:</b> ${e.max}</p>
            <button ${e.max === 0 ? "disabled" : ""}
              onclick="registerEvent(${e.id})">
              ${e.max === 0 ? "Full" : "Register"}
            </button>
          </div>
        `;
      });
    });
}

// ================================
// REGISTER EVENT (SINGLE VERSION ✅)
// ================================
function registerEvent(eventId) {
  fetch("/Event_Management_System/RegisterEventServlet", {
    method: "POST",
    credentials: "same-origin",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    },
    body: "eventId=" + eventId
  })
  .then(r => r.text())
  .then(res => {
    if (res === "success") {
      alert("Registered successfully!");
      loadUpcomingEvents();
    } else if (res === "already") {
      alert("Already Registered");
    } else if (res === "full") {
      alert("No seats available");
    } else if (res === "unauthorized") {
      alert("Session expired. Please login again.");
      logout();
    } else {
      alert("Error registering");
    }
  });
}


// ================================
// LOGOUT
// ================================
function logout() {
  sessionStorage.clear();
  showPublicNav();
  loadPage("about.html");
}
