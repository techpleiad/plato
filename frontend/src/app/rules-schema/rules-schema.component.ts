import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { rulesTemplate } from '../rulesTemplate';


@Component({
  selector: 'app-rules-schema',
  templateUrl: './rules-schema.component.html',
  styleUrls: ['./rules-schema.component.css']
})
export class RulesSchemaComponent implements OnInit {

  ruleJsonSchema: string = "";

  constructor(private dialogRef: MatDialogRef<RulesSchemaComponent>,@Inject(MAT_DIALOG_DATA) public data: rulesTemplate) {
    this.ruleJsonSchema = JSON.stringify(data.rule,null,2);
    dialogRef.disableClose = true;
   }

  ngOnInit(): void {
  }
  closeDialog(){
    this.dialogRef.close(RulesSchemaComponent);
  }

}
