import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-datatype-inputs',
  templateUrl: './datatype-inputs.component.html',
  styleUrls: ['./datatype-inputs.component.css']
})
export class DatatypeInputsComponent implements OnInit {

  datatype: string = "";
  typeInputs: string[] = [];
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {
    this.datatype = data.datatype;
    this.typeInputs = data.typeInputs;
    console.log(this.datatype);
    console.log(this.typeInputs);
  }

  ngOnInit(): void {
  }

}
