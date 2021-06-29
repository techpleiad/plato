import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import * as toJsonSchema from 'to-json-schema';
import * as yaml from 'yaml';

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

  constructor() {
   }

  ngOnInit(): void {
  }
  modifyProfileData(event: any){
    this.editorContent = event;
    this.showJsonSchema();
  }
  
  showJsonSchema(){
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
        this.jsonSchema = JSON.stringify(toJsonSchema(jsonObject));
      }
    }   
  }
  
  updateJsonSchemaContent(event: string){
    this.finalJsonSchema = event;
    this.jsonSchemaEdited.emit(this.finalJsonSchema);
    //console.log(this.finalJsonSchema);
  }

}
