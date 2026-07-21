(function () {
    function esCorreoValido(valor) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor);
    }

    function elementoErrorPara(input) {
        return document.querySelector('[data-error-for="' + input.id + '"]');
    }

    function marcarCampo(input, valido, mensaje) {
        input.classList.toggle('valid', valido);
        input.classList.toggle('invalid', !valido);

        var elementoError = elementoErrorPara(input);
        if (elementoError) {
            elementoError.textContent = valido ? '' : mensaje;
        }
    }

    function initLogin() {
        var form = document.getElementById('form-login');
        if (!form) {
            return;
        }

        var correo = document.getElementById('correo');
        var contrasena = document.getElementById('contrasena');

        function validarCorreo() {
            var valido = correo.value.trim() !== '' && esCorreoValido(correo.value.trim());
            marcarCampo(correo, valido, 'Ingresa un correo electrónico válido.');
            return valido;
        }

        function validarContrasena() {
            var valido = contrasena.value.trim() !== '';
            marcarCampo(contrasena, valido, 'La contraseña es obligatoria.');
            return valido;
        }

        correo.addEventListener('input', validarCorreo);
        correo.addEventListener('blur', validarCorreo);
        contrasena.addEventListener('input', validarContrasena);
        contrasena.addEventListener('blur', validarContrasena);

        form.addEventListener('submit', function (evento) {
            var correoValido = validarCorreo();
            var contrasenaValida = validarContrasena();
            if (!correoValido || !contrasenaValida) {
                evento.preventDefault();
            }
        });
    }

    function initRegistro() {
        var form = document.getElementById('form-registro');
        if (!form) {
            return;
        }

        var nombre = document.getElementById('nombre');
        var apellidoPaterno = document.getElementById('apellidoPaterno');
        var correo = document.getElementById('correo');
        var contrasena = document.getElementById('contrasena');
        var confirmarContrasena = document.getElementById('confirmarContrasena');

        var requisitos = {
            longitud: document.querySelector('[data-requisito="longitud"]'),
            letra: document.querySelector('[data-requisito="letra"]'),
            numero: document.querySelector('[data-requisito="numero"]')
        };

        function marcarRequisito(clave, cumplido) {
            var elemento = requisitos[clave];
            if (elemento) {
                elemento.classList.toggle('cumplido', cumplido);
            }
        }

        function validarRequerido(input, mensaje) {
            var valido = input.value.trim() !== '';
            marcarCampo(input, valido, mensaje);
            return valido;
        }

        function validarCorreo() {
            var valido = correo.value.trim() !== '' && esCorreoValido(correo.value.trim());
            marcarCampo(correo, valido, 'Ingresa un correo electrónico válido.');
            return valido;
        }

        function validarContrasena() {
            var valor = contrasena.value;
            var tieneLongitud = valor.length >= 8;
            var tieneLetra = /[A-Za-z]/.test(valor);
            var tieneNumero = /\d/.test(valor);

            marcarRequisito('longitud', tieneLongitud);
            marcarRequisito('letra', tieneLetra);
            marcarRequisito('numero', tieneNumero);

            var valido = tieneLongitud && tieneLetra && tieneNumero;
            marcarCampo(contrasena, valido, 'La contraseña no cumple los requisitos.');
            return valido;
        }

        function validarConfirmacion() {
            var valido = confirmarContrasena.value !== '' && confirmarContrasena.value === contrasena.value;
            marcarCampo(confirmarContrasena, valido, 'Las contraseñas no coinciden.');
            return valido;
        }

        nombre.addEventListener('input', function () {
            validarRequerido(nombre, 'El nombre es obligatorio.');
        });
        nombre.addEventListener('blur', function () {
            validarRequerido(nombre, 'El nombre es obligatorio.');
        });

        apellidoPaterno.addEventListener('input', function () {
            validarRequerido(apellidoPaterno, 'El apellido paterno es obligatorio.');
        });
        apellidoPaterno.addEventListener('blur', function () {
            validarRequerido(apellidoPaterno, 'El apellido paterno es obligatorio.');
        });

        correo.addEventListener('input', validarCorreo);
        correo.addEventListener('blur', validarCorreo);

        contrasena.addEventListener('input', function () {
            validarContrasena();
            if (confirmarContrasena.value !== '') {
                validarConfirmacion();
            }
        });
        contrasena.addEventListener('blur', validarContrasena);

        confirmarContrasena.addEventListener('input', validarConfirmacion);
        confirmarContrasena.addEventListener('blur', validarConfirmacion);

        form.addEventListener('submit', function (evento) {
            var esValido = [
                validarRequerido(nombre, 'El nombre es obligatorio.'),
                validarRequerido(apellidoPaterno, 'El apellido paterno es obligatorio.'),
                validarCorreo(),
                validarContrasena(),
                validarConfirmacion()
            ].every(Boolean);

            if (!esValido) {
                evento.preventDefault();
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        initLogin();
        initRegistro();
    });
})();
