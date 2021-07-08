import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CustomValidateReportComponent } from './custom-validate-report/custom-validate-report.component';
import { HomeComponent } from './home/home.component';
import { JsonSchemaWriterComponent } from './json-schema-writer/json-schema-writer.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RulesComponent } from './rules/rules.component';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'rules', component: RulesComponent },
  { path: 'schema-writer', component: JsonSchemaWriterComponent},
  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
