import { Component, OnInit } from '@angular/core';
import * as toJsonSchema from 'to-json-schema';
import * as yaml from 'yaml';

@Component({
  selector: 'app-json-schema-writer',
  templateUrl: './json-schema-writer.component.html',
  styleUrls: ['./json-schema-writer.component.css']
})
export class JsonSchemaWriterComponent implements OnInit {

  jsonSchema: any;
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
        console.log("editor mode is YAML");
        let jsonObject = yaml.parse(this.editorContent);
        this.jsonSchema = JSON.stringify(toJsonSchema(jsonObject));
      }
    }
    
      
  }

}
