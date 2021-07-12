import { Component, Input, OnInit, AfterViewInit, Output, EventEmitter, OnChanges } from '@angular/core';
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
export class CustomValidateReportComponent implements OnInit, OnChanges {
  
  @Input() service: string = "";
  @Input() branch: string = "";
  @Input() profile: string = "";
  @Output() gotCVReport = new EventEmitter();

  cusVal!: customValidate;
  displayedColumns = ['position', 'parentProperty', 'property', 'errorMsg'];
  dataSource: any[] = [];
  result: any;
  showTable = true;

  constructor(private _rulesDataService: RulesDataService) {
    this.cusVal = new customValidate();
  }
  //ngOnInit(){}
  ngOnChanges(){}
  ngOnInit(): void {
    console.log("here");
    //this.dataSource.push({position: 1, property: "temp", errorMsg: "errormm"});
    this.cusVal.services=[]
    this.cusVal.branches=[]
    this.cusVal.profiles=[]

    this.cusVal.services.push(this.service);
    this.cusVal.branches.push(this.branch);
    this.cusVal.profiles.push(this.profile);
    this.cusVal.email = {sendEmail: false, recipients: []};

    
      this._rulesDataService.sendCustomValidateEmail(this.cusVal).subscribe(data=>{
        this.result = JSON.parse(JSON.stringify(data));
        this.showTable = this.result[0].customValidateReportList.length>0;
        
        if(this.showTable){
          let parentProperty: string = this.result[0].customValidateReportList[0].property;
          let strList: string[] = this.result[0].customValidateReportList[0].validationMessages;
          let tempData: any[] = [];
          for(let i=0;i<strList.length;i++){
            let str: string = strList[i].slice(2);
            let idx = -1;
            for(let j=0;j<str.length;j++){
              if(str[j]===':'){
                idx = j;
                break;
              }
            }
            console.log("here");
            
            tempData.push({position: i+1, parentProperty: parentProperty, property: str.slice(0,idx), errorMsg: str.slice(idx+2)});
          }
          this.dataSource = tempData;
            
        }
        this.gotCVReport.emit();
        console.log(this.dataSource);
      });
    

    
    
  }

}
