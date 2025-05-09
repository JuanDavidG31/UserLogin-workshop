import { Component, OnInit } from '@angular/core';
import { AuthService} from '../security/AuthService';
import {HttpErrorResponse, HttpEvent} from '@angular/common/http';
import { Router } from '@angular/router';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {
  private objetoHttp: HttpClient;
  profilePicture: File | null = null;
  updateUsername: string = '';
  updatePassword: string = '';
  updateError: boolean = false;
  updateSuccess: boolean = false;
  currentUsername: string | null = null;
  image='';
  imagen='';
  mapa='';
  address='';
  protected contenido: any;

  constructor(private authService: AuthService,private router: Router,obj: HttpClient) {
    this.objetoHttp = obj;
  }
  traerMapa(): void {
    this.authService.mostrarMapa(this.address).subscribe({
      next: (response: any) => {
        let parsed = typeof response === 'string' ? JSON.parse(response) : response;
               this.mapa = parsed.mapUrl;
        console.log('Link recibido');
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error al  obtenerel link:', error);
      }
    });
  }
  ngOnInit(): void {
    const decodedToken = this.authService.getDecodedToken();
    this.currentUsername = decodedToken ? decodedToken.user : null;

    if (!this.currentUsername) {
      console.error('Token inválido o no disponible.');
      return;
    }

    this.objetoHttp.get<any[]>('http://localhost:8081/auth/showAllEncrypted')
      .subscribe(
        (usuarios) => {
          const usuarioLogueado = usuarios.find(u => u.user === this.currentUsername);
          if (usuarioLogueado) {
            this.image = usuarioLogueado.image;
            this.imagen = this.authService.obtenerUrlArchivo(this.image);
            this.address=usuarioLogueado.address;
          } else {
            console.warn('Usuario no encontrado en la lista');
          }
        },
        (error) => {
          console.error('Error al obtener usuarios:', error);
        }
      );
  }

  onFileSelected(event: any): void {
    this.profilePicture = event.target.files[0];
  }

  onSubmitProfilePhotoForm(): void {
    if (this.profilePicture) {
      console.log('Archivo seleccionado:', this.profilePicture);
    } else {
      console.log('No se ha seleccionado ningún archivo.');
    }
  }

  onSubmitUpdateUserForm(): void {
    this.updateError = false;
    this.updateSuccess = false;

    const updateData: any = {};
    if (this.updateUsername) {
      updateData.user = this.updateUsername;
    }
    if (this.updatePassword) {
      updateData.password = this.updatePassword;
    }

    if (Object.keys(updateData).length > 0) {
      this.authService.updateUser(updateData)
        .subscribe(
          (response) => {
            console.log('Usuario actualizado correctamente:', response);
            this.updateSuccess = true;
            this.resetUpdateForm();
          },
          (error: HttpErrorResponse) => {
            console.error('Error al actualizar usuario:', error);
            this.updateError = true;
          }
        );
    } else {
      this.updateError = true;
      console.warn('No se proporcionaron datos para actualizar.');
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/user']);
  }

  resetUpdateForm(): void {
    this.updateUsername = '';
    this.updatePassword = '';
  }
}
