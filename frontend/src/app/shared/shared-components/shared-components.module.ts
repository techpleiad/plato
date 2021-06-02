import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DropdownComponent } from './dropdown/dropdown.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import { BtnComponent } from './btn/btn.component';




@NgModule({
  declarations: [
    DropdownComponent,
    BtnComponent
  ],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatDialogModule,
    MatSelectModule,
    MatInputModule
  ],
  exports: [
    DropdownComponent,
    BtnComponent,
    
  ]
  
})
export class SharedComponentsModule { }

