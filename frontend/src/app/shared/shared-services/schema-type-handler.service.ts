import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { BehaviorSubject, Observable } from 'rxjs';
import { DatatypeInputsComponent } from 'src/app/datatype-inputs/datatype-inputs.component';

@Injectable({
  providedIn: 'root'
})
export class SchemaTypeHandlerService {

  dataTypeMap = new Map()
  inputValuesList: any[] = [];
  private _includeParams$ = new BehaviorSubject<any>(this.inputValuesList);

  constructor(public dialog: MatDialog) {
    this.dataTypeMap.set('string',[
      {param: "minLength", inputType: "integer"},
      {param: "maxLength", inputType: "integer"},
      {param: "const", inputType: "string"},
      {param: "pattern", inputType: "string"},
      {param: "enum", inputType: "list"},
    ]);
    this.dataTypeMap.set('integer',[
      {param: "minimum", inputType: "integer"},
      {param: "maximum", inputType: "integer"},
      {param: "const", inputType: "integer"}
    ]);
    this.dataTypeMap.set('object',[
      {param: "additionalProperties", inputType: "boolean"},
      {param: "required", inputType: "list"}
    ]);
    this.dataTypeMap.set('array',[
      {param: "uniqueItems", inputType: "boolean"},
      {param: "minItems", inputType: "integer"},
      {param: "maxItems", inputType: "integer"}
    ]);
  }


  takeInputs(datatype: string){
    const dialogRef = this.dialog.open(DatatypeInputsComponent,{
      data: {
        datatype: datatype,
        typeInputs: this.dataTypeMap.get(datatype)
      },
      height: 'auto',
      width: '500px',
    });

    dialogRef.afterClosed().subscribe((result: any)=>{
      console.log(result);
      for(let key in result){
          this.inputValuesList.push({
            [key]:result[key]
          })
        
      }
      console.log(this.inputValuesList);
      this._includeParams$.next(this.inputValuesList);
      
    });
  }
  get includeParams$():any{
    return this._includeParams$;
  }
  resetInputValues(){
    this.inputValuesList = [];
  }


}
