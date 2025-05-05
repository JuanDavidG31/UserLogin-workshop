import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService} from '../security/AuthService';
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
  registrationError = false;
  registrationSuccess = false;

  name = '';
  cedula = '';
  username = '';
  tPassword = '';
  country = '';
  address = '';
  image:any;

  constructor(private router: Router, private authService: AuthService) { }

  iniciarSesion() {
    this.loginError = false;
    this.authService.login({ user: this.user, password: this.password })
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
    this.registrationError = false;
    this.registrationSuccess = false;
    this.authService.register({
      user: this.username,
      password: this.tPassword,
      name: this.name,
      cedula: this.cedula,
      country: this.country,
      address: this.address
    })
      .subscribe(
        (response) => {
          console.log('Cuenta creada exitosamente:', response);
          this.registrationSuccess = true;
          this.resetInpusCreate();
        },
        (error: HttpErrorResponse) => {
          console.error('Error al crear cuenta:', error);
          this.registrationError = true;
        }
      );
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
    this.image = null;
  }
}
