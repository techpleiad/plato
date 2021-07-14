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
import { WarningDialogComponent } from './warning-dialog/warning-dialog.component';
import {MatIconModule} from '@angular/material/icon';
import { FlexLayoutModule } from '@angular/flex-layout';



@NgModule({
  declarations: [
    DropdownComponent,
    BtnComponent,
    HeaderComponent,
    HeadingPipePipe,
    WarningDialogComponent
  ],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatDialogModule,
    MatSelectModule,
    MatInputModule,
    MatToolbarModule,
    MatIconModule,
    FlexLayoutModule
  ],
  exports: [
    DropdownComponent,
    BtnComponent,
    HeaderComponent,
    WarningDialogComponent,
    HeadingPipePipe
  ],
  providers: [
    { provide: 'APPLICATION', useValue: APPLICATION},
  ]
  
})
export class SharedComponentsModule { }

