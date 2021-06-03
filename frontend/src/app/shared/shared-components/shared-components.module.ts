import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DropdownComponent } from './dropdown/dropdown.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import { BtnComponent } from './btn/btn.component';
import { HeaderComponent } from './header/header.component';
import {MatToolbarModule} from '@angular/material/toolbar';




@NgModule({
  declarations: [
    DropdownComponent,
    BtnComponent,
    HeaderComponent
  ],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatDialogModule,
    MatSelectModule,
    MatInputModule,
    MatToolbarModule
  ],
  exports: [
    DropdownComponent,
    BtnComponent,
    HeaderComponent
  ]
  
})
export class SharedComponentsModule { }
