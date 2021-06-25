import { Component, OnInit } from '@angular/core';
import * as toJsonSchema from 'to-json-schema';

@Component({
  selector: 'app-json-schema-writer',
  templateUrl: './json-schema-writer.component.html',
  styleUrls: ['./json-schema-writer.component.css']
})
export class JsonSchemaWriterComponent implements OnInit {

  jsonSchema: any;
  constructor() {
   }

  ngOnInit(): void {
  }
  modifyProfileData(event: any){
    console.log(event);
    let jsonObject = (JSON.parse(event));
    console.log(toJsonSchema(jsonObject));
    this.jsonSchema = JSON.stringify(toJsonSchema(jsonObject));
  }

}
