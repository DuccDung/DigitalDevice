using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class Device
{
    public string DeviceId { get; set; } = null!;

    public string? RoomId { get; set; }

    public string NameDevice { get; set; } = null!;

    public string? PhotoPath { get; set; }

    public string? CategoryDeviceId { get; set; }

    public virtual CategoryDevice? CategoryDevice { get; set; }

    public virtual ICollection<DeviceFunction> DeviceFunctions { get; set; } = new List<DeviceFunction>();

    public virtual ICollection<History> Histories { get; set; } = new List<History>();

    public virtual Room? Room { get; set; }
}
