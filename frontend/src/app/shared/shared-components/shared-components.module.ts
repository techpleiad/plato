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
import { HeadingPipePipe } from '../../heading-pipe.pipe';
import { APPLICATION } from '../shared-constants';




@NgModule({
  declarations: [
    DropdownComponent,
    BtnComponent,
    HeaderComponent,
    HeadingPipePipe
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
    HeaderComponent,
    HeadingPipePipe
  ],
  providers: [
    { provide: 'APPLICATION', useValue: APPLICATION},
  ]
  
})
export class SharedComponentsModule { }

