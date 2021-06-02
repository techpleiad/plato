import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { AddServiceButtonComponent } from './add-service-button/add-service-button.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AddNewServiceComponent } from './add-new-service/add-new-service.component';
import { MatDialogModule } from '@angular/material/dialog'
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import {MatRadioModule} from '@angular/material/radio';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { MatCardModule } from '@angular/material/card';
import {MatChipsModule} from '@angular/material/chips';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import { MatIconModule} from '@angular/material/icon';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    AddServiceButtonComponent,
    AddNewServiceComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatDialogModule,
    FormsModule,
    HttpClientModule,
    MatRadioModule,
    MatCheckboxModule,
    MatCardModule,
    MatChipsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
