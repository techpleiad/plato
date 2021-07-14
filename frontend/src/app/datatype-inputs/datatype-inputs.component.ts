import { Component, Inject, OnInit } from '@angular/core';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {COMMA, ENTER} from '@angular/cdk/keycodes';

@Component({
  selector: 'app-datatype-inputs',
  templateUrl: './datatype-inputs.component.html',
  styleUrls: ['./datatype-inputs.component.css']
})
export class DatatypeInputsComponent implements OnInit {

  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  datatype: string = "";
  typeInputs: any[] = [];
  inputValuesObject: any;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private dialogRef: MatDialogRef<DatatypeInputsComponent>) {
    dialogRef.disableClose = true;
    this.datatype = data.datatype;
    this.typeInputs = data.typeInputs;
    this.inputValuesObject = {};
    for(let i=0;i<this.typeInputs.length;i++){
      this.inputValuesObject[this.typeInputs[i].param] = null;
    }
    console.log(this.datatype);
    console.log(this.typeInputs);
  }

  ngOnInit(): void {
  }
  tempShow(){
    console.log("Sending inputs from the dialog");
    this.dialogRef.close(this.inputValuesObject);
  }
  removeFromList(l: string,idx: number){
    const index = this.inputValuesObject[this.typeInputs[idx].param].indexOf(l);

    if (index >= 0) {
      this.inputValuesObject[this.typeInputs[idx].param].splice(index, 1);
    }
  }
  addInList(event: MatChipInputEvent,idx: number){
    console.log("adding in list");
    const value = (event.value || '').trim();
    if(this.inputValuesObject[this.typeInputs[idx].param]===null){
      this.inputValuesObject[this.typeInputs[idx].param] = [];
    }
    if (value) {
      this.inputValuesObject[this.typeInputs[idx].param].push(value);
    }

    if(this.inputValuesObject[this.typeInputs[idx].param].length===0){
      this.inputValuesObject[this.typeInputs[idx].param] = null;
    }

    event.chipInput!.clear();
  }
  closeDialog(){
    this.dialogRef.close(DatatypeInputsComponent);
  }

}
