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
    .then(r => r.text())
    .then(html => {
      document.getElementById("app-content").innerHTML = html;

      if (page === "faculty-login.html") initFacultyLogin();
      if (page === "student-login.html") initStudentLogin();
      if (page === "create-event.html") initCreateEvent();
      if (page === "student-dashboard.html") loadStudentDashboard();
      if (page === "student-upcoming-events.html") loadStudentUpcomingEvents();
      if (page === "faculty-upcoming-events.html") loadFacultyUpcomingEvents();
      if (page === "registered-students.html") loadRegisteredStudents();
      if (page === "edit-event.html") initEditEvent();
    });
}

// ================================
// NAVBAR HELPERS
// ================================
function nav(title, links) {
  document.getElementById("navTitle").innerText = title;
  document.getElementById("navLinks").innerHTML = links;
}

function showPublicNav() {
  nav("Event Scheduler", `
    <a href="#" onclick="loadPage('about.html')">Home</a>
    <a href="#" onclick="loadPage('faculty-login.html')">Faculty</a>
    <a href="#" onclick="loadPage('student-login.html')">Student</a>
  `);
}

function showFacultyNav() {
  nav("Faculty Dashboard", `
    <a href="#" onclick="loadPage('faculty-dashboard.html')">Dashboard</a>
    <a href="#" onclick="loadPage('create-event.html')">Create Event</a>
    <a href="#" onclick="loadPage('faculty-upcoming-events.html')">Upcoming</a>
    <a href="#" onclick="loadPage('registered-students.html')">Registrations</a>
    <a href="#" onclick="logout()">Logout</a>
  `);
}

function showStudentNav() {
  nav("Student Dashboard", `
    <a href="#" onclick="loadPage('student-dashboard.html')">Dashboard</a>
    <a href="#" onclick="loadPage('student-upcoming-events.html')">Upcoming</a>
    <a href="#" onclick="logout()">Logout</a>
  `);
}

// ================================
// FACULTY LOGIN
// ================================
function initFacultyLogin() {
  const form = document.getElementById("facultyLoginForm");
  if (!form) return;

  form.onsubmit = e => {
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
  };
}

// ================================
// STUDENT LOGIN
// ================================
function initStudentLogin() {
  const form = document.getElementById("studentLoginForm");
  if (!form) return;

  form.onsubmit = e => {
    e.preventDefault();

    fetch("/Event_Management_System/StudentLoginServlet", {
      method: "POST",
      body: new FormData(form)
    })
    .then(r => r.text())
    .then(res => {
      if (res.trim() === "success") {
        showStudentNav();
        loadPage("student-dashboard.html");
      } else {
        document.getElementById("loginError").innerText =
          "Invalid student credentials";
      }
    });
  };
}

// ================================
// STUDENT DASHBOARD
// ================================
function loadStudentDashboard() {
  fetch("/Event_Management_System/GetStudentProfileServlet")
    .then(r => r.json())
    .then(d => {
      document.getElementById("studentName").innerText = "Student";
      document.getElementById("studentEmail").innerText = d.email;
    });
}

// ================================
// STUDENT UPCOMING EVENTS (REGISTER)
// ================================
function loadStudentUpcomingEvents() {
  fetch("/Event_Management_System/StudentUpcomingEventsServlet")
    .then(r => r.json())
    .then(events => {
      const box = document.getElementById("studentEventsContainer");
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
            <p><b>Seats Left:</b> ${e.seats}</p>

            <button
              ${e.registered || e.seats <= 0 ? "disabled" : ""}
              onclick="registerEvent(${e.id})">
              ${e.registered ? "Already Registered" :
                e.seats <= 0 ? "Full" : "Register"}
            </button>
          </div>
        `;
      });
    });
}

// ================================
// REGISTER EVENT
// ================================
function registerEvent(eventId) {
  fetch("/Event_Management_System/RegisterEventServlet", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "eventId=" + eventId
  })
  .then(r => r.text())
  .then(res => {
    res = res.trim();

    if (res === "success") {
      alert("Registered successfully!");
      loadStudentUpcomingEvents();
    } else if (res === "already_registered") {
      alert("You have already registered for this event.");
    } else if (res === "full") {
      alert("Event is full.");
    } else if (res === "session_expired") {
      alert("Session expired. Login again.");
      logout();
    } else {
      alert("Registration error.");
    }
  });
}

// ================================
// FACULTY UPCOMING EVENTS
// ================================
function loadFacultyUpcomingEvents() {
  fetch("/Event_Management_System/FacultyUpcomingEventsServlet")
    .then(r => r.json())
    .then(events => {
      const box = document.getElementById("eventsContainer");
      box.innerHTML = "";

      events.forEach(e => {
        box.innerHTML += `
          <div class="event-card">
            <h3>${e.name}</h3>
            <p><b>Date:</b> ${e.start} → ${e.end}</p>
            <p><b>Venue:</b> ${e.venue}</p>
            <p><b>Seats Left:</b> ${e.seats}</p>

            <div class="faculty-actions">
              <button class="edit-btn" onclick="editEvent(${e.id})">Edit</button>
              <button class="delete-btn" onclick="deleteEvent(${e.id})">Delete</button>
            </div>
          </div>
        `;
      });
    });
}

// ================================
// FACULTY REGISTERED STUDENTS (CORRECT WORKING VERSION)
// ================================
function loadRegisteredStudents() {
  fetch("/Event_Management_System/FacultyRegistrationsServlet")
    .then(r => r.json())
    .then(events => {
      const box = document.getElementById("registeredContainer");
      box.innerHTML = "";

      if (events.length === 0) {
        box.innerHTML = "<p>No registrations yet</p>";
        return;
      }

      events.forEach(ev => {
        box.innerHTML += `
          <div class="faculty-card">
            <h3>${ev.eventName}</h3>
            <div class="faculty-details">
              ${
                ev.students.length === 0 
                ? "<p>No students registered</p>"
                : ev.students.map(s => `<p>${s}</p>`).join("")
              }
            </div>
          </div>
        `;
      });
    });
}

// ================================
// EDIT EVENT
// ================================
function editEvent(id) {
  sessionStorage.setItem("editEventId", id);
  loadPage("edit-event.html");
}

// ================================
// LOAD EVENT TO EDIT
// ================================
function initEditEvent() {
  const id = sessionStorage.getItem("editEventId");
  if (!id) return;

  fetch("/Event_Management_System/GetEventByIdServlet?id=" + id)
    .then(r => r.json())
    .then(e => {
      const f = document.getElementById("editEventForm");
      f.name.value = e.name;
      f.start_date.value = e.start;
      f.end_date.value = e.end;
      f.venue.value = e.venue;
      f.max_participants.value = e.max;
    });

  document.getElementById("editEventForm").onsubmit = ev => {
    ev.preventDefault();

    const fd = new FormData(ev.target);
    fd.append("id", id);

    fetch("/Event_Management_System/UpdateEventServlet", {
      method: "POST",
      body: fd
    })
    .then(r => r.text())
    .then(res => {
      if (res === "success") {
        alert("Event updated");
        loadPage("faculty-upcoming-events.html");
      } else {
        alert("Update failed");
      }
    });
  };
}

// ================================
// DELETE EVENT
// ================================
function deleteEvent(id) {
  if (!confirm("Delete this event?")) return;

  fetch("/Event_Management_System/DeleteEventServlet", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "eventId=" + id
  })
  .then(() => loadFacultyUpcomingEvents());
}

// ================================
// LOGOUT
// ================================
function logout() {
  fetch("/Event_Management_System/LogoutServlet")
    .finally(() => {
      showPublicNav();
      loadPage("about.html");
    });
}
