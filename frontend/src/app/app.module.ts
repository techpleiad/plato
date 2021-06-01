import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { HttpClientModule } from '@angular/common/http';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { DisplayServicesComponent } from './display-services/display-services.component';
import { WorkspaceDialogueComponent } from './workspace-dialogue/workspace-dialogue.component';
import { ContentDisplayComponent } from './content-display/content-display.component';
import { BranchProfileInputComponent } from './branch-profile-input/branch-profile-input.component';

@NgModule({
  declarations: [
    AppComponent,
    DisplayServicesComponent,
    WorkspaceDialogueComponent,
    ContentDisplayComponent,
    BranchProfileInputComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatIconModule,
    MatFormFieldModule,
    MatDialogModule,
    MatSelectModule,
    MatInputModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
