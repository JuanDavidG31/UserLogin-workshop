import { Component, OnInit } from '@angular/core';
import { AuthService} from '../user/AuthService';
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

  constructor(private authService: AuthService,private router: Router) { }

  ngOnInit(): void {
    const decodedToken = this.authService.getDecodedToken();
    this.currentUsername = decodedToken ? decodedToken.user : null;
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
