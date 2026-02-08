document.addEventListener("DOMContentLoaded", () => {
  showPublicNav();
  loadPage("about.html");
});


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

function initCreateEvent() {
  const form = document.getElementById("eventForm");
  if (!form) return;

  form.onsubmit = e => {
    e.preventDefault();

    const fd = new FormData(form);
    const params = new URLSearchParams();
    for (let [key, value] of fd.entries()) params.append(key, value);

    fetch("/Event_Management_System/EventServlet", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params.toString()
    })
    .then(r => r.text())
    .then(res => {
      res = res.trim();

      if (res === "success") {
        alert("Event created successfully!");
        loadPage("faculty-upcoming-events.html");
      } else if (res === "unauthorized") {
        alert("Unauthorized: Please login as faculty.");
      } else {
        alert("Error creating event!");
      }
    })
    .catch(err => {
      console.error(err);
      alert("Network error!");
    });
  };
}

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

function loadStudentDashboard() {
  fetch("/Event_Management_System/GetStudentProfileServlet")
    .then(r => r.json())
    .then(d => {
      document.getElementById("studentName").innerText = "Student";
      document.getElementById("studentEmail").innerText = d.email;
    });
}


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


function loadRegisteredStudents() {
  fetch("/Event_Management_System/GetRegisteredStudentsServlet")
    .then(r => r.json())
    .then(data => {
      window.allRegistrations = data; 
      renderRegisteredStudents(data);
    });
}

function loadRegisteredStudents() {
  fetch("/Event_Management_System/GetRegisteredStudentsServlet")
    .then(r => r.json())
    .then(data => {
      const box = document.getElementById("registeredContainer");
      box.innerHTML = "";

      if (data.length === 0) {
        box.innerHTML = "<p>No registrations yet</p>";
        return;
      }

      data.forEach(item => {
        const studentsHTML = item.students.length > 0
          ? item.students.map(s => `<div class="student-email">${s}</div>`).join("")
          : `<div class="no-students">No students registered</div>`;

        box.innerHTML += `
          <div class="registration-card">
            <h3>${item.event}</h3>
            ${studentsHTML}
          </div>
        `;
      });
    });
}


function editEvent(id) {
  sessionStorage.setItem("editEventId", id);
  loadPage("edit-event.html");
}


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

function deleteEvent(id) {
  if (!confirm("Delete this event?")) return;

  fetch("/Event_Management_System/DeleteEventServlet", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "eventId=" + id
  })
  .then(() => loadFacultyUpcomingEvents());
}

function logout() {
  fetch("/Event_Management_System/LogoutServlet")
    .finally(() => {
      showPublicNav();
      loadPage("about.html");
    });
}
