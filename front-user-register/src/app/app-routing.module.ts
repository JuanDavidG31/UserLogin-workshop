import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {UserComponent} from './user/user.component';
import { UserAdminComponent } from './user-admin/user-admin.component';
import {MenuComponent} from './menu/menu.component';
import { AuthGuard} from './security/AuthGuard';
import {MenuAdminComponent} from './menu-admin/menu-admin.component';

const routes: Routes = [
  { path: 'login', component: UserComponent },
  { path: 'loginAdmin', component: UserAdminComponent },
  { path: 'menu', component: MenuComponent, canActivate: [AuthGuard] },
  { path: 'menuAdmin', component: MenuAdminComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
