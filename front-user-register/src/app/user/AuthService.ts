import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

interface AuthResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'TU_URL_DEL_BACKEND/login';
  private tokenKey = 'authToken';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {
    console.log('Initial isAuthenticated:', this.hasToken());

  }

  login(credentials: any): Observable<boolean> {
    return this.http.post<AuthResponse>(this.apiUrl, credentials)
      .pipe(
        map(response => {
          if (response && response.token) {
            this.saveToken(response.token);
            this.isAuthenticatedSubject.next(true);
            console.log('Login successful, isAuthenticated is now true');
            return true;
          } else {
            this.removeToken();
            this.isAuthenticatedSubject.next(false);
            console.log('Login failed, isAuthenticated is now false');
            return false;
          }
        })
      );
  }

  logout(): void {
    this.removeToken();
    this.isAuthenticatedSubject.next(false);
  }

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  removeToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  private hasToken(): boolean {
    const has = !!localStorage.getItem(this.tokenKey);
    console.log('Checking hasToken:', has); // Agrega esta línea
    return has;
  }

  getDecodedToken(): any | null {
    const token = this.getToken();
    if (token) {
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
      } catch (error) {
        console.error('Error al decodificar el token:', error);
        return null;
      }
    }
    return null;
  }

  getUserId(): string | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken ? decodedToken.sub : null;
  }

}
