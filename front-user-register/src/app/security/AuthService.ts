import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

interface AuthResponse {
  token: string;
}

interface UpdateUserRequest {
  username?: string;
  password?: string;
}

interface RegisterRequest {
  user: string;
  password: string;
  name: string;
  cedula: string;
  coutry: string;
  address: string;
  image:string;
  rol:string;
}

interface AuthResponse {
  token: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUsername: string | null = null;
  private apiUrlLogin = 'http://localhost:8081/auth/login';
  private apiUrlRegister = 'http://localhost:8081/auth/register';
  private apiUrlUpdate = 'http://localhost:8081/user/update';
  private apiUrlSubirArchivo = 'http://localhost:8081/auth/subir-archivo';
  private apiUrlObtenerArchivoBase = 'http://localhost:8081/auth/archivo';
  private apiUrlActualizarFotoPerfil = 'http://localhost:8081/user/actualizar-foto-perfil';
  private apiMaps='http://localhost:8081/map/map';
  private apiUrlUser = 'http://localhost:8081/user';
  private tokenKey = 'authToken';
  private apiUrlPaises='http://localhost:8081/auth/paises';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {
    console.log('Initial isAuthenticated:', this.hasToken());
  }
  getPaises(): Observable<{ nombre: string }[]> {

    return this.http.get<{ nombre: string }[]>(this.apiUrlPaises,{headers: this.createAuthHeaders()});

  }


  private createAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    if (token) {
      return new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      });
    }
    return new HttpHeaders({ 'Content-Type': 'application/json' });
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrlUser}/showAllEncrypted`, { headers: this.createAuthHeaders() });
  }

  mostrarMapa(address: string): Observable<any> {
    const params = new HttpParams().set('address', address);
    return this.http.get(this.apiMaps, { params, responseType: 'text', headers: this.createAuthHeaders() });
  }

  subirArchivo(file: File): Observable<any> {
    const formData = new FormData();
    formData.append("archivo", file);
    const token = this.getToken();
    const headers = token ? new HttpHeaders({ 'Authorization': `Bearer ${token}` }) : new HttpHeaders();
    return this.http.post(this.apiUrlSubirArchivo, formData, { headers });
  }

  obtenerUrlArchivo(nombreArchivo: string): string {
    return `${this.apiUrlObtenerArchivoBase}/${nombreArchivo}`;
  }

  login(credentials: any): Observable<boolean> {
    return this.http.post<AuthResponse>(this.apiUrlLogin, credentials)
      .pipe(
        map(response => {
          if (response && response.token) {
            this.saveToken(response.token);
            localStorage.setItem('role', response.role);

            if (response.role === "ADMIN") {
              this.isAuthenticatedSubject.next(true);
              console.log('Login successful, isAuthenticated is now true');
              return true;
            }

            if (response.role === "USER") {
              this.isAuthenticatedSubject.next(true);
              console.log('Login successful, isAuthenticated is now true');
              return true;
            }

            this.removeToken();
            this.isAuthenticatedSubject.next(false);
            console.log('Login failed, role is not ADMIN or USER');
            return false;
          } else {
            this.removeToken();
            this.isAuthenticatedSubject.next(false);
            console.log('Login failed, isAuthenticated is now false');
            return false;
          }
        })
      );
  }

  register(userData: RegisterRequest): Observable<any> {
    const params = new HttpParams().set('rol', userData.rol);
    return this.http.post(this.apiUrlRegister, userData, { params });
  }

  updateUser(updateData: {id?: number; user?: string; password?: string }): Observable<any> {
    const token = this.getToken();
    if (!token) {
      return new Observable(observer => observer.error('No token available'));
    }

    const userId = this.getUserId();
    if (!userId) {
      return new Observable(observer => observer.error('No user ID found in token'));
    }

    let params = new HttpParams();
    if (updateData.id) {
      params = params.set('id', updateData.id);
    }

    if (updateData.user) {
      params = params.set('newUsername', updateData.user);
    }

    if (updateData.password) {
      params = params.set('newPassword', updateData.password);
    }

    return this.http.put(`${this.apiUrlUpdate}`, null, { params, headers: this.createAuthHeaders() });
  }

  actualizarFotoDePerfil(id: number, archivo: File): Observable<any> {
    const formData = new FormData();
    formData.append('id', id.toString());
    formData.append('archivo', archivo, archivo.name);

    const token = this.getToken();
    const headers = token ? new HttpHeaders({ 'Authorization': `Bearer ${token}` }) : new HttpHeaders();

    return this.http.put(`${this.apiUrlActualizarFotoPerfil}`, formData, { headers });
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
    console.log('Checking hasToken:', has);
    return has;
  }

  getDecodedToken(): any | null {
    const token = this.getToken();
    if (token) {
      try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
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
