import { Component, Input, OnInit } from '@angular/core';
import { customValidate } from '../customValidate';
import { RulesDataService } from '../shared/shared-services/rules-data.service';

export interface ValidationDisplay{
  position: number;
  property: string;
  errorMsg: string;
}

@Component({
  selector: 'app-custom-validate-report',
  templateUrl: './custom-validate-report.component.html',
  styleUrls: ['./custom-validate-report.component.css']
})
export class CustomValidateReportComponent implements OnInit {
  
  @Input() service = "device-manager";
  @Input() branch = "dev";
  @Input() profile = "dev";

  cusVal!: customValidate;
  displayedColumns: string[] = ['position', 'property', 'errorMsg'];
  dataSource: ValidationDisplay[] = [];
  result: any;

  constructor(private _rulesDataService: RulesDataService) {
    this.cusVal = new customValidate();
  }

  ngOnInit(): void {
    this.cusVal.services.push(this.service);
    this.cusVal.branches.push(this.branch);
    this.cusVal.profiles.push(this.profile);
    this.cusVal.email = {sendEmail: false, recipients: []};
    this._rulesDataService.sendCustomValidateEmail(this.cusVal).subscribe(data=>{
      this.result = JSON.parse(JSON.stringify(data));
      let strList: string[] = this.result[0].customValidateReportList[0].validationMessages;
      for(let i=0;i<strList.length;i++){
        let str: string = strList[i].slice(2);
        let idx = -1;
        for(let j=0;j<str.length;j++){
          if(str[j]===':'){
            idx = j;
            break;
          }
        }
        this.dataSource.push({position: i+1, property: str.slice(0,idx), errorMsg: str.slice(idx+2)});
      }
      console.log(this.dataSource);
    });
  }

}
