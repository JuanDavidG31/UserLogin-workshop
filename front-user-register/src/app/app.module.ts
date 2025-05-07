import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';

import { UserComponent } from './user/user.component';
import { TableComponent } from './table/table.component';
import { UserAdminComponent } from './user-admin/user-admin.component';
import { MenuComponent } from './menu/menu.component';
import {HTTP_INTERCEPTORS } from '@angular/common/http';

import { AuthService} from './security/AuthService';
import { AuthInterceptor } from './security/AuthInterceptor';


@NgModule({
  declarations: [
    AppComponent,
    UserComponent,
    TableComponent,
    UserAdminComponent,
    MenuComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule
  ],
  providers: [
    provideHttpClient(),
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }

  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
