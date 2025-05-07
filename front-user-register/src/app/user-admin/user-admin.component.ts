import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../security/AuthService';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-user-admin',
  standalone: false,
  templateUrl: './user-admin.component.html',
  styleUrl: './user-admin.component.scss'
})
export class UserAdminComponent {
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

  irPaginaSesion(){
    this.router.navigate(['/login']);
  }

  crearCuenta() {
    this.registrationError = false;
    this.registrationSuccess = false;

    this.authService.register({
      user: this.username,
      password: this.tPassword,
      name: this.name,
      cedula: this.cedula,
      coutry: this.country,
      address: this.address,
      rol: "ADMIN"
    })
      .subscribe({
        next: (response: any) => {
          alert("Usuario Creado con exito")
          console.log('Respuesta del backend al crear cuenta:', response);

          this.registrationSuccess = true;
          this.resetInpusCreate();
        },
        error: (error: HttpErrorResponse) => {
          alert("No se pudo crear el usuario")
          console.error('Error al crear cuenta:', error);

          if (error.status === 409) {
            alert("El usuario ya existe")
            console.error('El usuario ya existe.');
          } else if (error.status === 400) {
            alert("Datos inválidos")
            console.error('Datos inválidos');
          }

          this.registrationError = true;
        }
      });
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
