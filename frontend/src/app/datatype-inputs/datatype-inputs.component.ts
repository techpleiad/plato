import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-datatype-inputs',
  templateUrl: './datatype-inputs.component.html',
  styleUrls: ['./datatype-inputs.component.css']
})
export class DatatypeInputsComponent implements OnInit {

  datatype: string = "";
  typeInputs: any[] = [];
  inputValuesObject: any;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private dialogRef: MatDialogRef<DatatypeInputsComponent>) {
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

}
