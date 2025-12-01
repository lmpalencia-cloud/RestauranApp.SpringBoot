let currentTableId = null;
let currentOrder = null;

document.addEventListener('DOMContentLoaded', () => { /* grid ya listo */ });

async function openTable(el){
  currentTableId = el.dataset.id;
  document.getElementById('modalTitle').innerText = el.dataset.name;

  // asegurar orden abierta (o nula)
  const ordRes = await fetch(`/worker/orders/current/${currentTableId}`);
  currentOrder = ordRes.ok ? await ordRes.json() : null;

  // cargar menú (crea /api/menu que devuelva JSON si no pasas PRODUCTS desde servidor)
  let products = [];
  try {
    const menuRes = await fetch('/api/menu'); // crea control sencillo que retorne List<Product>
    products = menuRes.ok ? await menuRes.json() : [];
  } catch(e){ products = window.PRODUCTS || []; }

  renderMenu(products);
  renderOrder(currentOrder);
  wireButtons();

  new bootstrap.Modal(document.getElementById('tableModal')).show();
}

function renderMenu(products){
  const cont = document.getElementById('menuList');
  cont.innerHTML = '';
  products.forEach(p => {
    const row = document.createElement('div');
    row.className = 'menu-item';
    row.innerHTML = `<div><strong>${p.name}</strong><div class="text-muted" style="font-size:.85rem;">${p.description || ''}</div></div>
                     <div>
                       $${Number(p.price).toFixed(2)}
                       <button class="btn btn-sm btn-outline-primary ms-2" onclick="addItem(${p.id})">Agregar</button>
                     </div>`;
    cont.appendChild(row);
  });
}

async function ensureOrder(){
  if(currentOrder && currentOrder.id) return currentOrder;
  const res = await fetch(`/worker/orders/current/${currentTableId}/ensure`, { method:'POST' });
  currentOrder = await res.json();
  // pinta mesa como ocupada
  const card = document.getElementById('table-'+currentTableId);
  card.classList.remove('free','paid-not-clean');
  card.classList.add('busy');
  return currentOrder;
}

async function addItem(productId){
  const order = await ensureOrder();
  const res = await fetch(`/worker/orders/${order.id}/items`, {
    method: 'POST',
    headers: {'Content-Type':'application/x-www-form-urlencoded'},
    body: `productId=${productId}&quantity=1`
  });
  if(!res.ok){ alert('No se pudo agregar el producto'); return; }
  // refrescar orden
  const ordRes = await fetch(`/worker/orders/current/${currentTableId}`);
  currentOrder = await ordRes.json();
  renderOrder(currentOrder);
}

function renderOrder(order){
  const itemsDiv = document.getElementById('orderItems');
  const totalSpan = document.getElementById('orderTotal');
  itemsDiv.innerHTML = '';
  if(!order || !order.items || order.items.length === 0){
    itemsDiv.innerHTML = '<div class="text-muted">Sin pedidos</div>';
    totalSpan.innerText = '0.00';
    document.getElementById('btnViewInvoice').href = '#';
    return;
  }
  order.items.forEach(it => {
    const line = document.createElement('div');
    line.className = 'd-flex justify-content-between';
    line.innerHTML = `<div>${it.product.name} x${it.quantity}</div>
                      <div>$${(it.price * it.quantity).toFixed(2)}</div>`;
    itemsDiv.appendChild(line);
  });
  totalSpan.innerText = Number(order.total).toFixed(2);
  document.getElementById('btnViewInvoice').href = `/worker/order/view/${order.id}`;
}

function wireButtons(){
  document.getElementById('btnCreateOrder').onclick = async () => { await ensureOrder(); };
  document.getElementById('btnPay').onclick = async () => {
    if(!currentOrder || !currentOrder.id) return;
    const res = await fetch(`/worker/orders/${currentOrder.id}/pay`, { method:'POST' });
    if(res.ok){
      // limpiar UI total y pasar mesa a gris (pagada pero no limpia)
      currentOrder = null;
      renderOrder(null);
      const card = document.getElementById('table-'+currentTableId);
      card.classList.remove('busy','free'); 
      card.classList.add('paid-not-clean');
      // mantener checkbox para marcar en verde cuando esté lista
    } else {
      alert('Error al pagar la factura');
    }
  };
}

async function toggleClean(tableId, cleaned){
  await fetch(`/worker/tables/${tableId}/clean?cleaned=${cleaned}`, { method:'PUT' });
  const card = document.getElementById('table-'+tableId);
  if(cleaned){
    card.classList.remove('busy','paid-not-clean'); 
    card.classList.add('free');
  } else {
    // si no está limpia y no ocupada, queda gris
    card.classList.remove('free');
    if(!card.classList.contains('busy')) card.classList.add('paid-not-clean');
  }
}
