import { Component, OnInit } from '@angular/core';
import { AuthService} from '../security/AuthService';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss'
})
export class MenuComponent implements OnInit {
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

  constructor(private authService: AuthService, private router: Router) { }

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
    this.profilePicture = event.target.files[0];
  }

  onSubmitProfilePhotoForm(): void {
    if (this.profilePicture) {
      console.log('Archivo seleccionado:', this.profilePicture);
    } else {
      console.log('No se ha seleccionado ningún archivo.');
    }
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
    this.router.navigate(['/login']);
  }

  resetUpdateForm(): void {
    this.updateUsername = '';
    this.updatePassword = '';
  }
}
