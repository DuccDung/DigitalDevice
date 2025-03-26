using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class Room
{
    public string RoomId { get; set; } = null!;

    public string? HomeId { get; set; }

    public string Name { get; set; } = null!;

    public string? PhotoPath { get; set; }

    public virtual ICollection<Device> Devices { get; set; } = new List<Device>();

    public virtual Home? Home { get; set; }
}
