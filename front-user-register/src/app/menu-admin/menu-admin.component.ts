import {Component, OnInit} from '@angular/core';
import { AuthService} from '../security/AuthService';
import {HttpErrorResponse, HttpEvent} from '@angular/common/http';
import { Router } from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {UserComponent} from "../user/user.component";
@Component({
  selector: 'app-menu-admin',
  standalone: false,
  templateUrl: './menu-admin.component.html',
  styleUrl: './menu-admin.component.scss'
})
export class MenuAdminComponent implements OnInit{
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
  id:any;
  uploadingPicture: boolean = false;
  uploadMessage: string = '';
  constructor(private authService: AuthService, private router: Router) { }
  loadUserData(): void {
    const decodedToken = this.authService.getDecodedToken();
    this.currentUsername = decodedToken ? decodedToken.user : null;

    if (!this.currentUsername) {
      console.error('Token inválido o no disponible.');
      return;
    }

    this.authService.getAllUsers().subscribe(
      (usuarios) => {
        const usuarioLogueado = usuarios.find(u => u.user === this.currentUsername);
        if (usuarioLogueado) {
          this.image = usuarioLogueado.image;
          this.imagen = this.authService.obtenerUrlArchivo(this.image);
          this.address = usuarioLogueado.address;
          this.id = usuarioLogueado.id;
        } else {
          console.warn('Usuario no encontrado en la lista');
        }
      },
      (error) => {
        console.error('Error al obtener usuarios:', error);
      }
    );
  }
  actualizarFotoPerfil(): void {
    if (!this.profilePicture || !this.id) {
      this.uploadMessage = 'Por favor, selecciona una foto de perfil.';
      return;
    }

    this.uploadingPicture = true;
    this.uploadMessage = 'Subiendo foto...';

    this.authService.actualizarFotoDePerfil(this.id, this.profilePicture).subscribe({
      next: (response: any) => {
        this.uploadingPicture = false;
        this.uploadMessage = response.message;
        if (response.success) {
          console.log('Foto de perfil actualizada:', response)
          this.loadUserData();
          const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
          if (fileInput) {
            fileInput.value = '';
          }
          this.profilePicture = null;
        } else {
          console.error('Error al actualizar la foto de perfil:', response);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.uploadingPicture = false;
        this.uploadMessage = 'Error al subir la foto de perfil.';
        console.error('Error al subir la foto de perfil:', error);
      }
    });
  }
  traerMapa(): void {
    this.authService.mostrarMapa(this.address).subscribe({
      next: (response: any) => {
        let parsed = typeof response === 'string' ? JSON.parse(response) : response;
        this.mapa = parsed.mapa;
        console.log('Link recibido', this.mapa);
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

    this.authService.getAllUsers().subscribe(
      (usuarios) => {
        const usuarioLogueado = usuarios.find(u => u.user === this.currentUsername);
        if (usuarioLogueado) {
          this.image = usuarioLogueado.image;
          this.imagen = this.authService.obtenerUrlArchivo(this.image);
          this.address = usuarioLogueado.address;
          this.id = usuarioLogueado.id;
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
    this.profilePicture = event.target.files[0] as File;
  }

  update(): void {
    this.updateError = false;
    this.updateSuccess = false;

    const updateData: any = {};
    if (this.id) {
      updateData.id = this.id;
    }

    if (this.updateUsername) {
      updateData.user = this.updateUsername;
    }
    if (this.updatePassword) {
      updateData.password = this.updatePassword;
    }

    if (Object.keys(updateData).length > 0) {
      this.authService.updateUser(updateData)
        .subscribe({
          next: (response: any) => {
            alert('Usuario Actualizado con éxito');
            console.log('Usuario actualizado correctamente:', response);
            this.updateSuccess = true;
            this.resetUpdateForm();
          },
          error: (error: HttpErrorResponse) => {
            console.error('Error al actualizar usuario:', error);
            this.updateError = true;
          },
        });
    }
    else {
      this.updateError = true;
      console.warn('No se proporcionaron datos para actualizar.');
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/loginAdmin']);
  }

  resetUpdateForm(): void {
    this.updateUsername = '';
    this.updatePassword = '';
  }
}
