import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {UserComponent} from './user/user.component';
import {MenuComponent} from './menu/menu.component';

const routes: Routes = [
  {
    path: '', component: UserComponent,
  },
  {
    path: 'menu', component: MenuComponent,
  }


];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
