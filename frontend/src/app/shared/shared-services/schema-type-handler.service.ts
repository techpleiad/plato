import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DatatypeInputsComponent } from 'src/app/datatype-inputs/datatype-inputs.component';

@Injectable({
  providedIn: 'root'
})
export class SchemaTypeHandlerService {

  dataTypeMap = new Map()
  
  constructor(public dialog: MatDialog) {
    this.dataTypeMap.set('string',["minLength", "maxLength", "const", "enum"]);
    this.dataTypeMap.set('integer',["minValue","maxValue","const"]);
  }
  takeInputs(datatype: string){
    const dialogRef = this.dialog.open(DatatypeInputsComponent,{
      data: {
        datatype: datatype,
        typeInputs: this.dataTypeMap.get(datatype)
      },
      height: '400px',
      width: '400px',
    });
    //console.log(this.dataTypeMap.get(type));
  }
}
