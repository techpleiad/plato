import { Component, OnInit } from '@angular/core';
import * as toJsonSchema from 'to-json-schema';

@Component({
  selector: 'app-json-schema-writer',
  templateUrl: './json-schema-writer.component.html',
  styleUrls: ['./json-schema-writer.component.css']
})
export class JsonSchemaWriterComponent implements OnInit {

  constructor() {
    let object = {
      India: "In",
      Europe: "Eu"
    };
    //console.log(JSON.stringify(object));
    let jsonSchema = toJsonSchema(object);
    console.log(JSON.stringify(jsonSchema,null,2));
    
   }

  ngOnInit(): void {
  }
  modifyProfileData(event: any){
    /*
    console.log(JSON.stringify(event));
    let jsonString = event.replace(/\n/g,"");
    jsonString = jsonString.replace(/\t/g,"");
    jsonString = jsonString.replace(/   /g,"");
    console.log(JSON.stringify(jsonString));*/
    console.log(event);
    let jsonObject = (JSON.parse(event));
    console.log(toJsonSchema(jsonObject));
    
  }

}
