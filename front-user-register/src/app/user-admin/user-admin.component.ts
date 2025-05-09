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
  image='';
  map='';
  imagen: File | null = null;

  constructor(private router: Router, private authService: AuthService) { }
  traerMapa(): string {
    this.authService.mostrarMapa('portal 80').subscribe({
      next: (respuesta: string) => {
        this.map = respuesta;
        console.log('Link recibido:', this.map);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error al obtener el link:', error);
      }
    });
    return this.map;
  }
  iniciarSesion() {
    this.loginError = false;
    this.authService.login({ user: this.user, password: this.password })
      .subscribe(
        (success) => {
          if (success) {
            const role = localStorage.getItem('role');
            if (role === 'ADMIN') {
              this.router.navigate(['/menuAdmin']);
            } else {
              this.loginError = true;
              alert('Acceso denegado. Solo los administradores pueden ingresar.');
              this.authService.logout();
            }
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

  onImageSelected(event: any): void {
    this.imagen = event.target.files[0] as File;
  }

  crearCuenta() {
    this.registrationError = false;
    this.registrationSuccess = false;

    if (this.imagen) {
      this.authService.subirArchivo(this.imagen).subscribe({
        next: (response: any) => {
          console.log('Respuesta de subida de imagen:', response);
          this.image = response.nombreArchivo;
          this.registrarUsuario();
        },
        error: (error: HttpErrorResponse) => {
          alert('Error al subir la imagen.');
          console.error('Error al subir la imagen:', error);
          this.registrationError = true;
        },
      });
    } else {
      this.registrarUsuario();
    }
  }

  registrarUsuario() {
    this.authService.register({
      user: this.username,
      password: this.tPassword,
      name: this.name,
      cedula: this.cedula,
      coutry: this.country,
      address: this.address,
      image: this.image,
      rol: "ADMIN"
    }).subscribe({
      next: (response: any) => {
        alert('Usuario Creado con éxito');
        console.log('Respuesta del backend al crear cuenta:', response);
        this.registrationSuccess = true;
        this.resetInpusCreate();
      },
      error: (error: HttpErrorResponse) => {
        alert('No se pudo crear el usuario');
        console.error('Error al crear cuenta:', error);

        if (error.status === 409) {
          alert('El usuario ya existe');
          console.error('El usuario ya existe.');
        } else if (error.status === 400) {
          alert('Datos inválidos');
          console.error('Datos inválidos');
        }

        this.registrationError = true;
      },
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
    this.image = '';
  }
}
