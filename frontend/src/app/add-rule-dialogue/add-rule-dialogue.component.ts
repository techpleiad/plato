import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { addRuleTemplate } from '../addRuleTemplate';
import { microService } from '../microService';
import { CodemirrorService } from '../shared/shared-services/codemirror.service';
import { DataManagerService } from '../shared/shared-services/data-manager.service';
import { RulesDataService } from '../shared/shared-services/rules-data.service';

@Component({
  selector: 'app-add-rule-dialogue',
  templateUrl: './add-rule-dialogue.component.html',
  styleUrls: ['./add-rule-dialogue.component.css']
})
export class AddRuleDialogueComponent implements OnInit {

  servicesSelected = new FormControl();
  branchesSelected = new FormControl();
  profilesSelected = new FormControl();

  nRule!: addRuleTemplate;
  ruleData: string = "";
  mservices: microService[] = [];
  serviceList: string[] = [];
  branchList: string[] = [];
  profileList: string[] = [];

  visibleProgressSpinner = false;
  isRuleOnPropertyValid = true;
  isServiceValid = true;

  constructor(private dialogRef: MatDialogRef<AddRuleDialogueComponent>,@Inject(MAT_DIALOG_DATA) private data: addRuleTemplate, private _dataManagerService: DataManagerService, private _codemirrorService: CodemirrorService, private _rulesDataService: RulesDataService) {
    this.nRule = new addRuleTemplate();
  }

  

  ngOnInit(): void {
    this._dataManagerService.getServicesList().subscribe(data => {
      this.mservices = JSON.parse(JSON.stringify(data));
      for(let i=0;i<this.mservices.length;i++){
        this.serviceList.push(this.mservices[i].service);
      }
    });
  }
  setFunction(){
    this.branchList = [];
    this.profileList = [];
    let serSel: string[] = [];
    if(this.servicesSelected.value!=null){
      serSel = this.servicesSelected.value;
      for(let i=0;i<serSel.length;i++){
        for(let j=0;j<this.mservices.length;j++){
          if(this.mservices[j].service===serSel[i]){
            let bList = this.mservices[j].branches.map((x:any) => x.name);
            this.branchList = this.branchList.concat(bList);
            let pList = this.mservices[i].profiles.map((x:any) => x.name);
            this.profileList = this.profileList.concat(pList);
          }
        }
      }
      this.branchList = [...new Set(this.branchList)];
      this.profileList = [...new Set(this.profileList)];
    }
  }


  updateRuleData(event: any){
    this.ruleData = event;
    console.log(typeof event);
  }


  addNewRule(){
    let sList: string[] = [];
    let bList: string[] = [];
    let pList: string[] = [];
    if(this.servicesSelected.value!=null) sList = this.servicesSelected.value;
    if(this.branchesSelected.value!=null) bList = this.branchesSelected.value;
    if(this.profilesSelected.value!=null) pList = this.profilesSelected.value;
    this.nRule.scope = {
      services: sList,
      branches: bList,
      profiles: pList
    };
    this.isRuleOnPropertyValid = this.nRule.ruleOnProperty.length>0;
    this.isServiceValid = sList.length>0;
    
    let temp = {
      
      "rule": {
        "id": "https://example.com/person.schema.json",
        "$schema": "https://json-schema.org/draft/2020-12/schema",
        "description": "something",
        "title": "Person",
      }
    }

    if(this.isRuleOnPropertyValid && this.isServiceValid){
      this.visibleProgressSpinner = true;
      //this.ruleData = this.ruleData.slice(1);
      //this.ruleData = this.ruleData.slice(0,this.ruleData.length-1);
      console.log(this.ruleData);
      temp["rule"] = JSON.parse(this.ruleData);
      temp["rule"]["id"] = "https://example.com/person.schema.json";
      temp["rule"]["$schema"] = "https://json-schema.org/draft/2020-12/schema";
      temp["rule"]["description"] = "something",
      temp["rule"]["title"] = "Person"
      console.log(temp);

      let s1 = JSON.stringify(temp,null,2), s2 = JSON.stringify(this.nRule,null,2);
      console.log(s1);
      console.log(this.ruleData);
      s1 = s1.slice(1);
      s1 = s1.slice(0,s1.length-1);
      s2 = s2.slice(1);
      s2 = s2.slice(0,s2.length-1);
      let newRule = "{"+s1+","+s2+"}";
      let addNewRule = JSON.parse(newRule);
      console.log(newRule);
      
      this._rulesDataService.addRule(addNewRule).subscribe(data=>{
        this.visibleProgressSpinner = false;
        this.dialogRef.close(addNewRule);
      });
    }
  }

}
