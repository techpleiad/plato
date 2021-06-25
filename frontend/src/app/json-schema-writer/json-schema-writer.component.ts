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
  constructor() {
   }

  ngOnInit(): void {
  }
  modifyProfileData(event: any){
    console.log(event);

    if(this.editorMode==="JSON"){
      if(event===""){
        this.jsonSchema = "";
      }
      else{
        let jsonObject = (JSON.parse(event));
        this.jsonSchema = JSON.stringify(toJsonSchema(jsonObject));
      }
    }
    else if(this.editorMode==="YAML"){
      console.log("editor mode is YAML");
      let jsonObject = yaml.parse(event);
      this.jsonSchema = JSON.stringify(toJsonSchema(jsonObject));
    }
      
  }

}
