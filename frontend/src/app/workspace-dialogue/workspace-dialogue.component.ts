import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { microService } from '../microService';
import { ProfileSpecTO } from '../shared/models/ProfileSpecTO';
import { ConfigFilesService } from '../shared/shared-services/config-files.service';
import { ProfileAggregatorService } from '../shared/shared-services/profile-aggregator.service';
import { SprimeraFilesService } from '../shared/shared-services/sprimera-files.service';

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

  isBranchReq = false;
  isProfileReq = false;
  canProfileDefault = false;

  displayData: any;

  profileSpecTOList: ProfileSpecTO[] = []; /// (contains all the profile as list for sprimera).

  visibleProgressSpinner = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: microService, private _configFiles: ConfigFilesService, 
  private _sprimeraFilesService: SprimeraFilesService, private _profileAggregatorService: ProfileAggregatorService) {
    this.functionList = ["merged","individual","sprimera"];
    this.mservice = data;
    this.branchValue = "";
    this.profileValue = "";

    this.profileList = this.mservice.profiles.map((x: any) => x.name);
    this.branchList = this.mservice.branches.map((x:any) => x.name);
    

   }

  ngOnInit(): void {
  }
  setFunction(functionValue: any){
    this.functionValue = functionValue;
    this.setBranchProfileReq();
  }
  setBranchProfileReq(){
    if(this.functionValue==="merged" || this.functionValue==="individual" || this.functionValue=="sprimera"){
      this.isBranchReq = true;
      this.isProfileReq = true;
    }
    if(this.functionValue==="individual"){
      this.canProfileDefault = true;
    }
    else{
      this.canProfileDefault = false;
    }
    
  }
  setBranch(branchValue: any){
    this.branchValue = branchValue;
    console.log("Branch is set to ",this.branchValue);
  }
  setProfile(profileValue: any){
    this.profileValue = profileValue;
    console.log("Profile is set to ",this.profileValue);
  }
  

  sendToCodeMirror(){
    // Progress Spinner 
    this.visibleProgressSpinner = true;

    ////////////////   SHOW MERGED AND INDIVIDUAL CONFIGURATION FILES /////
    if(this.functionValue==="merged" || this.functionValue==="individual"){
      this._configFiles.getFile(this.mservice.service,this.functionValue, this.branchValue,this.profileValue)
      .subscribe(data => {
        if(data){
          this.visibleProgressSpinner = false;
        }
        this.displayData = data;
        //console.log(this.displayData);
      });
    }
    //////////////   SHOW SPRIMERA  //////////////
    else if(this.functionValue==="sprimera"){
      this._sprimeraFilesService.getFiles("custom-manager",this.branchValue,this.profileValue).subscribe((data: any[]) =>{
        console.log(data);
        for(let i=0;i<data.length;i++){
        
          this.profileSpecTOList.push(new ProfileSpecTO(
            data[i].profile,
            data[i].yaml,
            data[i].jsonNode,
          ))
        }
        //console.log(this.profileSpecTOList);
        let aggregated = this._profileAggregatorService.aggregateProfiles(this.profileSpecTOList);
        console.log(aggregated);
        //setting displayData to the json content of aggregated File.
        this.displayData = JSON.stringify(aggregated.jsonContent,null,2);
      })
    }

  }

  

}
