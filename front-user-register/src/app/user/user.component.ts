import { Component } from '@angular/core';

@Component({
  selector: 'app-user',
  standalone: false,
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserComponent {
  user = '';
  password = '';

  name = '';
  cedula = '';
  username = '';
  tPassword = '';
  country = '';
  address = '';

  showMenu = false;

  constructor() {
  }

  iniciarSesion() {
    console.log('Usuario:', this.user);
    console.log('Contraseña:', this.password);
    this.showMenu = !this.showMenu;
    this.resetInpusLogin();
  }

  crearCuenta() {
    console.log('Nombre Completo:', this.name);
    console.log('Cédula:', this.cedula);
    console.log('Usuario:', this.username);
    console.log('Contraseña:', this.tPassword);
    console.log('País:', this.country);
    console.log('Dirección:', this.address);
    this.resetInpusCreate();
  }

  resetInpusLogin() {
    this.user = '';
    this.password = '';
  }

  resetInpusCreate() {
    this.name = '';
    this.cedula = '';
    this.username = '';
    this.tPassword = '';
    this.country = '';
    this.address = '';
  }
}
