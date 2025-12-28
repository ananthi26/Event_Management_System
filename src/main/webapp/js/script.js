let generatedCaptcha = "";

function generateCaptcha() {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  let captcha = "";
  for (let i = 0; i < 6; i++) {
    captcha += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  generatedCaptcha = captcha;
  document.getElementById("captcha-display").value = captcha;
}

document.getElementById("loginForm").addEventListener("submit", function(event) {
  const userCaptcha = document.getElementById("captcha").value.trim();

  if (userCaptcha === "") {
    alert("Please enter the captcha.");
    event.preventDefault();
    return;
  }

  if (userCaptcha !== generatedCaptcha) {
    alert("Captcha does not match. Please try again.");
    generateCaptcha(); 
    document.getElementById("captcha").value = "";
    event.preventDefault();
  }
});

window.onload = function () {
  document.querySelector('input[name="email"]').value = '';
  document.querySelector('input[name="password"]').value = '';
  document.getElementById('captcha').value = '';

  generateCaptcha();

  const urlParams = new URLSearchParams(window.location.search);
  const msg = urlParams.get('msg');

  if (msg === 'invalid') {
    alert('Invalid email or password.');
  } else if (msg === 'loggedin') {
    alert('User already logged in elsewhere.');
  } else if (msg === 'dberror') {
    alert('Database connection error. Please try again later.');
  }
};





