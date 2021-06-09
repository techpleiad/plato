import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { microService } from '../microService';
import { ProfileSpecTO, PropertyDetail } from '../shared/models/ProfileSpecTO';
import { ConfigFilesService } from '../shared/shared-services/config-files.service';
import { ProfileAggregatorService } from '../shared/shared-services/profile-aggregator.service';
import { SprimeraFilesService } from '../shared/shared-services/sprimera-files.service';
import * as diff from 'deep-diff'
import * as yaml from 'yaml';

@Component({
  selector: 'app-workspace-dialogue',
  templateUrl: './workspace-dialogue.component.html',
  styleUrls: ['./workspace-dialogue.component.css']
})
export class WorkspaceDialogueComponent implements OnInit {

  mservice:microService;

  profileList: string[] = [];
  branchList: string[] = [];
  functionList: string[] = [];
  
  functionValue: any;
  branchValue: any;
  profileValue: any;

  branch1Value: any;
  branch2Value: any;


  isBranchReq = false;
  isProfileReq = false;
  canProfileDefault = false;

  isBranch1Req = false;
  isBranch2Req = false;
 

  displayData!: string;
 
  displayData2!: string;


  propertyList: PropertyDetail[]=[];
  ownerList: string[] = [];


  differenceProperties: string[] = [];
  //twoCodemirrors = false; //two codemirrors required while checking consistency.


  visibleProgressSpinner = false;

  isConsistency = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: microService, private _configFiles: ConfigFilesService, 
  private _sprimeraFilesService: SprimeraFilesService, private _profileAggregatorService: ProfileAggregatorService) {
    this.functionList = ["show merged file","show individual file","sprimera","consistency across branch"];
    this.mservice = data;
    this.branchValue = "";
    this.profileValue = "";

    this.branch1Value = "";
    this.branch2Value = "";
 

    this.profileList = this.mservice.profiles.map((x: any) => x.name);
    this.branchList = this.mservice.branches.map((x:any) => x.name);
   }

  ngOnInit(): void {
  }
  setFunction(functionValue: any){
    this.functionValue = functionValue;
    this.isBranchReq = false;
    this.isProfileReq = false;
    this.canProfileDefault = false;
    this.isBranch1Req = false;
    this.isBranch2Req = false;

    this.isConsistency = false;

    //this.twoCodemirrors = false;


    this.setBranchProfileReq();
  }
  setBranchProfileReq(){
    if(this.functionValue==="show merged file" || this.functionValue==="show individual file" || this.functionValue=="sprimera"){
      this.isBranchReq = true;
      this.isProfileReq = true;
    }

    if(this.functionValue==="consistency across branch"){
      this.isBranch1Req = true;
      this.isBranch2Req = true;
      this.isProfileReq = true;
    }
  
    if(this.functionValue==="show individual file" || this.functionValue==="consistency across branch"){
      this.canProfileDefault = true;
    }
  }
  setBranch(branchValue: any){
    this.branchValue = branchValue;
  }

  setBranch1(branchValue: any){
    this.branch1Value = branchValue;
  }
  setBranch2(branchValue: any){
    this.branch2Value = branchValue;
  }
  setProfile(profileValue: any){
    this.profileValue = profileValue;
  }
  

  sendToCodeMirror(){
    // Progress Spinner 
    this.visibleProgressSpinner = true;

    // SHOW MERGED AND INDIVIDUAL FILES
    if(this.functionValue==="show merged file" || this.functionValue==="show individual file"){

      this._configFiles.getFile(this.mservice.service,this.functionValue, this.branchValue,this.profileValue)
      .subscribe(data => {
        this.propertyList = [];
        this.ownerList = [];
        this.visibleProgressSpinner = false;
        this.displayData = data;
      });
      
    }
    // CONSISTENCY ACROSS BRANCHES
    else if(this.functionValue==="consistency across branch"){

      this.isConsistency = true;
      this._configFiles.getFile(this.mservice.service,this.functionValue, this.branch1Value,this.profileValue)
      .subscribe(data => {
        
        this._configFiles.getFile(this.mservice.service,this.functionValue, this.branch2Value,this.profileValue)
        .subscribe(data2 => {  
          this.visibleProgressSpinner = false;
          this.displayData2 = data2;
        });
        this.displayData = data;
      });
      
    }
 
    // SPIMERA
    else if(this.functionValue==="sprimera"){
      let profileSpecTOList: ProfileSpecTO[] = [];
      this._sprimeraFilesService.getFiles(this.mservice.service,this.branchValue,this.profileValue).subscribe((data: any[]) =>{
        console.log(data);
        //Converting the fetched files into the format required by profile_aggregator service.
        for(let i=0;i<data.length;i++){
          profileSpecTOList.push(new ProfileSpecTO(
            `${data[i].service}-${data[i].profile}`,
            data[i].yaml,
            data[i].jsonNode,
          ))
        }
        // Mering Files 
        const aggregated = this._profileAggregatorService.aggregateProfiles(profileSpecTOList);
        if(aggregated){
          this.visibleProgressSpinner = false;
        }
        this.displayData = JSON.stringify(aggregated.jsonContent,null,2);
        this.propertyList = aggregated.propertyList;
        this.ownerList = profileSpecTOList.map(function(val){
          return val.profile;
        })
      })
    }

  }

  

}
