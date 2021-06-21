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

  constructor(@Inject(MAT_DIALOG_DATA) public data: rulesTemplate) {
    console.log(data.rule);
    this.ruleJsonSchema = JSON.stringify(data.rule);
    console.log(this.ruleJsonSchema);
    console.log(typeof this.ruleJsonSchema);
   }

  ngOnInit(): void {
  }

}
