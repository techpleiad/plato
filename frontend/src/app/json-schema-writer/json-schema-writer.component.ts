import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import * as toJsonSchema from 'to-json-schema';
import * as yaml from 'yaml';
import { WarningDialogComponent } from '../shared/shared-components/warning-dialog/warning-dialog.component';

@Component({
  selector: 'app-json-schema-writer',
  templateUrl: './json-schema-writer.component.html',
  styleUrls: ['./json-schema-writer.component.css']
})
export class JsonSchemaWriterComponent implements OnInit {

  @Output() jsonSchemaEdited = new EventEmitter();

  jsonSchema: any;
  finalJsonSchema: any;
  editorMode = "JSON";
  editorContent = "";

  constructor(@Inject('WARNING_DIALOG_PARAM') private WARNING_DIALOG_PARAM: any,public dialog: MatDialog) {
   }

  ngOnInit(): void {
  }
  modifyProfileData(event: any){
    this.editorContent = event;
    //this.showJsonSchema();
  }
  
  showJsonSchema(){
    //console.log(this.jsonSchema);
    //console.log(this.finalJsonSchema);
    if(this.jsonSchema!==this.finalJsonSchema){
      console.log("fine");
      const dialogRef = this.dialog.open(WarningDialogComponent,this.WARNING_DIALOG_PARAM);
      
      dialogRef.afterClosed().subscribe(result=>{
        if(result==="yes"){
          this.makeChanges(); 
        }
        else{
          console.log("No Changes Made");
        }
      });
    }
    else{
      this.makeChanges();
    }
       
  }
  makeChanges(){
    if(this.editorContent===""){
      this.jsonSchema = "";
    }
    else{
      if(this.editorMode==="JSON"){
        let jsonObject = (JSON.parse(this.editorContent));
        this.jsonSchema = JSON.stringify(toJsonSchema(jsonObject));
      }
      else if(this.editorMode==="YAML"){
        //console.log("editor mode is YAML");
        let jsonObject = yaml.parse(this.editorContent);
        this.jsonSchema = JSON.stringify(toJsonSchema(jsonObject),null,2);
      }
    }
  }
  updateJsonSchemaContent(event: string){
    this.finalJsonSchema = event;
    this.jsonSchemaEdited.emit(this.finalJsonSchema);
    //console.log(this.finalJsonSchema);
  }

}
