document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('[data-list-add]').forEach(function (boton) {
        boton.addEventListener('click', function () {
            var campo = boton.getAttribute('data-list-add');
            var contenedor = document.querySelector('[data-list="' + campo + '"]');

            var fila = document.createElement('div');
            fila.className = 'list-row';

            var input = document.createElement('input');
            input.type = 'text';
            input.name = campo;

            var quitar = document.createElement('button');
            quitar.type = 'button';
            quitar.className = 'btn btn-secondary';
            quitar.setAttribute('data-list-remove', '');
            quitar.textContent = 'Quitar';

            fila.appendChild(input);
            fila.appendChild(quitar);
            contenedor.appendChild(fila);
            input.focus();
        });
    });

    document.addEventListener('click', function (evento) {
        if (evento.target.hasAttribute('data-list-remove')) {
            evento.target.closest('.list-row').remove();
        }
    });
});
