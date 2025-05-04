import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService} from './AuthService';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-user',
  standalone: false,
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserComponent {
  user = '';
  password = '';
  loginError = false;

  name = '';
  cedula = '';
  username = '';
  tPassword = '';
  country = '';
  address = '';

  constructor(private router: Router, private authService: AuthService) { }

  iniciarSesion() {
    console.log('Usuario:', this.user);
    console.log('Contraseña:', this.password);

    this.authService.login({ username: this.user, password: this.password })
      .subscribe(
        (success) => {
          if (success) {
            this.router.navigate(['/menu']);
            this.resetInpusLogin();
          } else {
            this.loginError = true;
          }
        },
        (error: HttpErrorResponse) => {
          console.error('Error al iniciar sesión:', error);
          this.loginError = true;
        }
      );
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
    this.loginError = false;
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
