import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { microService } from '../microService';
import { ProfileSpecTO, PropertyDetail } from '../shared/models/ProfileSpecTO';
import { ConfigFilesService } from '../shared/shared-services/config-files.service';
import { ProfileAggregatorService } from '../shared/shared-services/profile-aggregator.service';
import { SprimeraFilesService } from '../shared/shared-services/sprimera-files.service';
import * as diff from 'deep-diff'

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
  //branch consistency
  branch1Value: any;
  branch2Value: any;
  //branch consistency

  isBranchReq = false;
  isProfileReq = false;
  canProfileDefault = false;
  //branch consistency
  isBranch1Req = false;
  isBranch2Req = false;
  //branch consistency

  displayData!: string;
  //branch consistency
  displayData2!: string;
  //branch consistency

  propertyList: PropertyDetail[]=[];
  ownerList: string[] = [];

  visibleProgressSpinner = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: microService, private _configFiles: ConfigFilesService, 
  private _sprimeraFilesService: SprimeraFilesService, private _profileAggregatorService: ProfileAggregatorService) {
    this.functionList = ["merged","individual","sprimera","consistency across branch"];
    this.mservice = data;
    this.branchValue = "";
    this.profileValue = "";
    //branch consistency
    this.branch1Value = "";
    this.branch2Value = "";
    //branch consistency

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

    this.setBranchProfileReq();
  }
  setBranchProfileReq(){
    if(this.functionValue==="merged" || this.functionValue==="individual" || this.functionValue=="sprimera"){
      this.isBranchReq = true;
      this.isProfileReq = true;
    }
    //branch consistency
    if(this.functionValue==="consistency across branch"){
      this.isBranch1Req = true;
      this.isBranch2Req = true;
      this.isProfileReq = true;
    }
    //branch consistency
    if(this.functionValue==="individual" || "consistency across branch"){
      this.canProfileDefault = true;
    }
    else{
      this.canProfileDefault = false;
    }
    
  }
  setBranch(branchValue: any){
    this.branchValue = branchValue;
    //console.log("Branch is set to ",this.branchValue);
  }
  //branch consistency
  setBranch1(branchValue: any){
    this.branch1Value = branchValue;
  }
  setBranch2(branchValue: any){
    this.branch2Value = branchValue;
  }
  //branch consistency
  setProfile(profileValue: any){
    this.profileValue = profileValue;
    //console.log("Profile is set to ",this.profileValue);
  }
  

  sendToCodeMirror(){
    // Progress Spinner 
    this.visibleProgressSpinner = true;

    ////////////////   SHOW MERGED AND INDIVIDUAL CONFIGURATION FILES /////
    if(this.functionValue==="merged" || this.functionValue==="individual"){
      this.propertyList = [];
      this.ownerList = [];
      this._configFiles.getFile(this.mservice.service,this.functionValue, this.branchValue,this.profileValue)
      .subscribe(data => {
        if(data){
          this.visibleProgressSpinner = false;
        }
        this.displayData = data;
        //console.log(this.displayData);
      });
    }
    //branch consistency
    else if(this.functionValue==="consistency across branch"){
      //this.propertyList = [];
      //this.ownerList = [];
      this._configFiles.getFile(this.mservice.service,this.functionValue, this.branch1Value,this.profileValue)
      .subscribe(data => {
        if(data){
          this._configFiles.getFile(this.mservice.service,this.functionValue, this.branch2Value,this.profileValue)
          .subscribe(data2 => {
            if(data2){
              this.visibleProgressSpinner = false;
            }
            this.displayData2 = data2;
            console.log(data2);
            let differences = diff.diff(data,data2);
            console.log(differences);
          });
        }
        this.displayData = data;
        console.log(data);
      });
      
    }
    //branch consistency
    //////////////   SHOW SPRIMERA  //////////////
    else if(this.functionValue==="sprimera"){
      //////////  Bringing All The Files /////
      let profileSpecTOList: ProfileSpecTO[] = [];
      this._sprimeraFilesService.getFiles(this.mservice.service,this.branchValue,this.profileValue).subscribe((data: any[]) =>{
        //Converting the fetched files into the format required by profile_aggregator service.
        for(let i=0;i<data.length;i++){
          profileSpecTOList.push(new ProfileSpecTO(
            data[i].profile,
            data[i].yaml,
            data[i].jsonNode,
          ))
        }
        ///////////   Merging All The Files  //////////
        const aggregated = this._profileAggregatorService.aggregateProfiles(profileSpecTOList);
        if(aggregated){
          this.visibleProgressSpinner = false;
        }
        //console.log(aggregated);
        
        //setting displayData to the json content of aggregated File.
        this.displayData = JSON.stringify(aggregated.jsonContent,null,2);
        //console.log(aggregated.propertyList);
        this.propertyList = aggregated.propertyList;
        
        this.ownerList = profileSpecTOList.map(function(val){
          return val.profile;
        })
      })
    }

  }

  

}
