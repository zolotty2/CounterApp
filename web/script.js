// Данные
let taData = [
  {id:1, serial:"VM-202501", inventory:"INV-001", location:"ТЦ Мега Белая Дача", model:"Necta Star 2025", status:"Работает", lastTO:"12.03.2026"},
  {id:2, serial:"VM-202502", inventory:"INV-002", location:"БЦ Альфа, СПб", model:"Saeco Prime", status:"На обслуживании", lastTO:"01.03.2026"},
  {id:3, serial:"VM-202503", inventory:"INV-003", location:"АЗС Лукойл #45", model:"Fuji Royal", status:"Работает", lastTO:"28.02.2026"}
];

let currentYear = 2026;

// Переключение экранов
document.querySelectorAll('.menu-item').forEach(item => {
  item.addEventListener('click', () => {
    document.querySelectorAll('.menu-item').forEach(i => i.classList.remove('active'));
    item.classList.add('active');

    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
    document.getElementById(`screen-${item.dataset.screen}`).classList.add('active');

    document.getElementById('page-title').textContent = item.textContent.trim();
  });
});

// Загрузка файла
document.getElementById('upload-btn').addEventListener('click', () => {
  document.getElementById('file-input').click();
});

document.getElementById('file-input').addEventListener('change', (e) => {
  if (e.target.files.length > 0) {
    alert(`Файл "${e.target.files[0].name}" загружен.\nВ реальной системе здесь будет парсинг и проверка данных.`);
  }
});

// Заполнение таблицы
function renderTable() {
  const tbody = document.getElementById('ta-body');
  tbody.innerHTML = '';

  taData.forEach(ta => {
    const statusClass = ta.status === "Работает" ? "status-green" : "status-yellow";
    
    const row = document.createElement('tr');
    row.innerHTML = `
      <td><strong>${ta.id}</strong></td>
      <td>${ta.serial}</td>
      <td>${ta.inventory}</td>
      <td>${ta.location}</td>
      <td>${ta.model}</td>
      <td><span class="status ${statusClass}">${ta.status}</span></td>
      <td>${ta.lastTO}</td>
      <td>
        <button class="btn btn-primary" style="padding:8px 16px; font-size:14px;">Редактировать</button>
      </td>
    `;
    tbody.appendChild(row);
  });
}

// Календарь
function renderCalendar() {
  const grid = document.getElementById('calendar-grid');
  grid.innerHTML = '';
  const months = ["Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"];

  months.forEach(month => {
    const monthDiv = document.createElement('div');
    monthDiv.className = 'month';
    monthDiv.innerHTML = `
      <h3>${month} ${currentYear}</h3>
      <div style="display:flex; flex-wrap:wrap; gap:4px; justify-content:center; margin-top:12px;">
        <div class="day status-green">5</div>
        <div class="day status-yellow">12</div>
        <div class="day status-red">18</div>
        <div class="day status-green">25</div>
      </div>
    `;
    grid.appendChild(monthDiv);
  });
}

function prevYear() { currentYear--; renderCalendar(); }
function nextYear() { currentYear++; renderCalendar(); }

// Drag & Drop
function allowDrop(ev) { ev.preventDefault(); }

function drag(ev) {
  ev.dataTransfer.setData("text", ev.target.id);
}

function drop(ev) {
  ev.preventDefault();
  const data = ev.dataTransfer.getData("text");
  ev.target.appendChild(document.getElementById(data));
}

// Инициализация
renderTable();
renderCalendar();

// Пример заявок
setTimeout(() => {
  document.getElementById('tasks-list').innerHTML = `
    <div class="task-card" draggable="true" ondragstart="drag(event)" id="t1">
      <strong>VM-202501</strong><br>Плановое ТО • ТЦ Мега
    </div>
    <div class="task-card" draggable="true" ondragstart="drag(event)" id="t2">
      <strong style="color:#f44336">VM-202502</strong><br>Авария • Срочно
    </div>
  `;

  document.getElementById('employees-list').innerHTML = `
    <div class="task-card" style="border-left-color:#4CAF50">
      Иванов И.И.<br><small>Свободен • 2 заявки сегодня</small>
    </div>
    <div class="task-card" style="border-left-color:#FF9800">
      Петров С.В.<br><small>Занят • 1 заявка сегодня</small>
    </div>
  `;
}, 600);
