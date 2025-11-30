document.addEventListener('DOMContentLoaded', () => {
  const inputs = document.querySelectorAll('input, textarea');
  inputs.forEach(i => {
    i.addEventListener('focus', () => i.style.boxShadow = '0 6px 18px rgba(43,138,239,0.12)');
    i.addEventListener('blur', () => i.style.boxShadow = 'none');
  });
});
