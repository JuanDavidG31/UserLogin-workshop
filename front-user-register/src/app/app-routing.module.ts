import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {UserComponent} from './user/user.component';
import {MenuComponent} from './menu/menu.component';
import { AuthGuard} from './user/AuthGuard';

const routes: Routes = [
  { path: 'login', component: UserComponent },
  { path: 'menu', component: MenuComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
