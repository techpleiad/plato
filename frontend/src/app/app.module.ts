import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AddNewServiceComponent } from './add-new-service/add-new-service.component';
import {MatRadioModule} from '@angular/material/radio';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { MatCardModule } from '@angular/material/card';
import {MatChipsModule} from '@angular/material/chips';
import {MatButtonModule} from '@angular/material/button';

import { FlexLayoutModule } from '@angular/flex-layout';
import { HttpClientModule } from '@angular/common/http';

import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatSnackBarModule} from '@angular/material/snack-bar';

import { FormsModule } from '@angular/forms';
import { DisplayServicesComponent } from './display-services/display-services.component';
import { WorkspaceDialogueComponent } from './workspace-dialogue/workspace-dialogue.component';
import { ContentDisplayComponent } from './content-display/content-display.component';
import { ProfileInputComponent } from './profile-input/profile-input.component';

import { SharedComponentsModule } from './shared/shared-components/shared-components.module';
import { HeadingPipePipe } from './heading-pipe.pipe';
import { AddSearchComponent } from './add-search/add-search.component';

import { MatPaginatorModule } from '@angular/material/paginator';
import { API_END_POINT, PROFILE_COLORS, WARNING_DIALOG_PARAM } from './shared/shared-constants';
import { CustomCodemirrorComponent } from './custom-codemirror/custom-codemirror.component';
import { MonacoEditorComponent } from './monaco-editor/monaco-editor.component';

import {  MonacoEditorModule } from 'ngx-monaco-editor';
import { ConsistencyAcrossBranchDialogueComponent } from './consistency-across-branch-dialogue/consistency-across-branch-dialogue.component';
import { DatePipe } from '@angular/common';
import { ConsistencyAcrossProfileDialogueComponent } from './consistency-across-profile-dialogue/consistency-across-profile-dialogue.component';
@NgModule({
  declarations: [
    AppComponent,
    AddNewServiceComponent,
    DisplayServicesComponent,
    WorkspaceDialogueComponent,
    ContentDisplayComponent,
    ProfileInputComponent,
    AddSearchComponent,
    CustomCodemirrorComponent,
    MonacoEditorComponent,
    ConsistencyAcrossBranchDialogueComponent,
    ConsistencyAcrossProfileDialogueComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatRadioModule,
    MatCheckboxModule,
    MatCardModule,
    MatChipsModule,
    MatButtonModule,
    MatIconModule,
    HttpClientModule,
    MatFormFieldModule,
    MatDialogModule,
    MatSelectModule,
    MatInputModule,
    FormsModule,
    MatProgressSpinnerModule,
    FlexLayoutModule,
    SharedComponentsModule,
    MatToolbarModule,
    MatPaginatorModule,
    MatSidenavModule,
    MatSnackBarModule,
    MonacoEditorModule.forRoot()
  ],
  providers: [
    { provide: 'PROFILE_COLORS', useValue: PROFILE_COLORS},
    {provide: 'API_END_POINT', useValue: API_END_POINT},
    {provide: 'WARNING_DIALOG_PARAM', useValue: WARNING_DIALOG_PARAM},
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
