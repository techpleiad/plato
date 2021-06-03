import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {MatIconModule} from '@angular/material/icon';
import { microService } from '../microService'
import { DataManagerService } from '../shared/shared-services/data-manager.service';
import { WorkspaceDialogueComponent } from '../workspace-dialogue/workspace-dialogue.component';
import {PageEvent} from '@angular/material/paginator';


@Component({
  selector: 'app-display-services',
  templateUrl: './display-services.component.html',
  styleUrls: ['./display-services.component.css']
})
export class DisplayServicesComponent implements OnInit {

  services: microService[];

  //paginator variables and functions
  paginatorServices: microService[]=[];
  pageIndex: number = 0;
  pageSize: number = 1;
  pageSizeOptions: number[] = [1, 10, 25, 100];

  initializePaginator(){
    this.paginatorServices=this.services.slice(0, Math.min((1)*this.pageSize,this.services.length));
  }
  addToPaginator(event: any){
    console.log(event);
    this.paginatorServices=[];
    let idx: number = event.pageIndex;
    let sz: number = event.pageSize;
    this.paginatorServices=this.services.slice(idx*sz, Math.min((idx+1)*sz,this.services.length));
  }
  //paginator variables and functions
  
  constructor(private _dataManagerServices: DataManagerService, public dialog: MatDialog) { 
    this.services = [];
    
  }
  logServices(){
    console.log(this.services);
  }
  openWorkspace(mservice: microService){
    const dialogRef = this.dialog.open(WorkspaceDialogueComponent,{
      data: mservice,
      height: '700px',
      width: '1200px',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  
  }

  ngOnInit(): void {

    
    this._dataManagerServices.getServicesList()
        .subscribe(data => {
          this.services = JSON.parse(JSON.stringify(data));
          console.log(data);
        });
    this.initializePaginator();
        
  }

}
